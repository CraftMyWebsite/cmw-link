package fr.AxelVatan.CMWLink.Common.WebServer.Injector;

import fr.AxelVatan.CMWLink.Common.WebServer.ChannelDecoder;
import fr.AxelVatan.CMWLink.Common.WebServer.DefaultRoutes;
import fr.AxelVatan.CMWLink.Common.WebServer.WebServer;
import io.netty.channel.Channel;

public class Injector {

	private NettyInjector injector;

	public Injector(WebServer ws) {
		injector = new NettyInjector() {
			@Override
			protected void injectChannel(Channel channel) {
				channel.pipeline().addFirst(new ChannelDecoder(ws));
			}
		};
		injector.inject();
		new DefaultRoutes(ws);
	}

	public void close() {
		injector.close();
	}
}