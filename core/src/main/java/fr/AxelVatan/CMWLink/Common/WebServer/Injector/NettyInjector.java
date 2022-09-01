package fr.AxelVatan.CMWLink.Common.WebServer.Injector;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandler;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import com.google.common.collect.Lists;

import fr.AxelVatan.CMWLink.Common.WebServer.Injector.Fuzzy.FuzzyReflection;

public abstract class NettyInjector {   

	private List<VolatileField> bootstrapFields = Lists.newArrayList();
	private boolean injected;
	private boolean closed;

	public synchronized void inject() {
		if (injected)
			throw new IllegalStateException("Cannot inject twice.");
		try {
			FuzzyReflection fuzzyServer = FuzzyReflection.fromClass(MinecraftReflection.getMinecraftServerClass());
			Method serverConnectionMethod = fuzzyServer.getMethodByParameters("getServerConnection", MinecraftReflection.getServerConnectionClass(), new Class[] {});
			Object server = fuzzyServer.getSingleton();
			Object serverConnection = serverConnectionMethod.invoke(server);
			final ChannelInboundHandler endInitProtocol = new ChannelInitializer<Channel>() {
				@Override
				protected void initChannel(Channel channel) throws Exception {
					try {
						injectChannel(channel);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			};
			final ChannelInboundHandler beginInitProtocol = new ChannelInitializer<Channel>() {
				@Override
				protected void initChannel(Channel channel) throws Exception {
					channel.pipeline().addLast(endInitProtocol);
				}
			};
			final ChannelHandler connectionHandler = new ChannelInboundHandlerAdapter() {
				@Override
				public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
					Channel channel = (Channel) msg;
					channel.pipeline().addFirst(beginInitProtocol);
					ctx.fireChannelRead(msg);
				}
			};
			bootstrapFields = getBootstrapFields(serverConnection);
			for (VolatileField field : bootstrapFields) {
				@SuppressWarnings("unchecked")
				final List<Object> list = (List<Object>) field.getValue();
				field.setValue(new BootstrapList(list, connectionHandler));
			}
			injected = true;
		} catch (Exception e) {
			throw new RuntimeException("Unable to inject channel futures.", e);
		}
	}

	protected abstract void injectChannel(Channel channel);

	private List<VolatileField> getBootstrapFields(Object serverConnection) {
		List<VolatileField> result = Lists.newArrayList();
		for (Field field : FuzzyReflection.fromObject(serverConnection, true).getFieldListByType(List.class)) {
			VolatileField volatileField = new VolatileField(field, serverConnection).toSynchronized();
			@SuppressWarnings("unchecked")
			List<Object> list = (List<Object>) volatileField.getValue();
			if (list.size() == 0 || list.get(0) instanceof ChannelFuture) {
				result.add(volatileField);
			}
		}
		return result;
	}

	public synchronized void close() {
		if (!closed) {
			closed = true;
			for (VolatileField field : bootstrapFields) {
				Object value = field.getValue();
				if (value instanceof BootstrapList) {
					((BootstrapList) value).close();
				}
				field.revertValue();
			}
		}
	}
}