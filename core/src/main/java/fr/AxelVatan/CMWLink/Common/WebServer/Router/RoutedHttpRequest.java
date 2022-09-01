package fr.AxelVatan.CMWLink.Common.WebServer.Router;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class RoutedHttpRequest {

	private @Getter ChannelHandlerContext ctx;
	private @Getter FullHttpRequest	request;

}
