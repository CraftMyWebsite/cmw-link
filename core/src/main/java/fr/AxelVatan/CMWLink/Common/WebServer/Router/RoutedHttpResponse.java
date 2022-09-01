package fr.AxelVatan.CMWLink.Common.WebServer.Router;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class RoutedHttpResponse {

	private @Getter FullHttpResponse res;
	private @Getter FullHttpRequest	request;

}
