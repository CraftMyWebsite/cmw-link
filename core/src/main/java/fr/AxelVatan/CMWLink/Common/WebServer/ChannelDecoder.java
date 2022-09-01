package fr.AxelVatan.CMWLink.Common.WebServer;

import java.util.List;
import java.util.NoSuchElementException;

import fr.AxelVatan.CMWLink.Common.WebServer.Injector.Handler;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;

public class ChannelDecoder extends ByteToMessageDecoder {

	private WebServer ws;

	public ChannelDecoder(WebServer ws) {
		this.ws = ws;
	}

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf buf, List<Object> list) throws Exception {
		if (buf.readableBytes() < 4) {
			return;
		}
		buf.retain(2);

		final int magic1 = buf.getUnsignedByte(buf.readerIndex());
		final int magic2 = buf.getUnsignedByte(buf.readerIndex() + 1);
		final int magic3 = buf.getUnsignedByte(buf.readerIndex() + 2);
		final int magic4 = buf.getUnsignedByte(buf.readerIndex() + 3);
		ChannelPipeline p = ctx.channel().pipeline();

		if (isHttp(magic1, magic2, magic3, magic4)) {
			ByteBuf copy = buf.copy();
			ctx.channel().config().setOption(ChannelOption.TCP_NODELAY, true);

			try {
				while (p.removeLast() != null);
			} catch (NoSuchElementException e) {

			}

			p.addLast("codec-http", new HttpServerCodec());
			p.addLast("aggregator", new HttpObjectAggregator(65536));
			p.addLast("handler", new Handler(ws));

			p.fireChannelRead(copy);
			buf.release();
			buf.release();
		} else {
			try {
				p.remove(this);
			} catch (NoSuchElementException e) {
				System.out.println("NoSuchElementException");
			}

			buf.release();
			buf.release();
		}
	}

	private boolean isHttp(int magic1, int magic2, int magic3, int magic4) {
		return magic1 == 'G' && magic2 == 'E' && magic3 == 'T' && magic4 == ' ' || // GET
				magic1 == 'P' && magic2 == 'O' && magic3 == 'S' && magic4 == 'T' || // POST
				magic1 == 'P' && magic2 == 'U' && magic3 == 'T' && magic4 == ' ' || // PUT
				magic1 == 'H' && magic2 == 'E' && magic3 == 'A' && magic4 == 'D' || // HEAD
				magic1 == 'O' && magic2 == 'P' && magic3 == 'T' && magic4 == 'I' || // OPTIONS
				magic1 == 'P' && magic2 == 'A' && magic3 == 'T' && magic4 == 'C' || // PATCH
				magic1 == 'D' && magic2 == 'E' && magic3 == 'L' && magic4 == 'E' || // DELETE
				magic1 == 'T' && magic2 == 'R' && magic3 == 'C' && magic4 == 'C' || // TRACE
				magic1 == 'C' && magic2 == 'O' && magic3 == 'N' && magic4 == 'N'; // CONNECT
	}
}