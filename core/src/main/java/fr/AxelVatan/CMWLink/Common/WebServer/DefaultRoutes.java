package fr.AxelVatan.CMWLink.Common.WebServer;

import fr.AxelVatan.CMWLink.Common.Config.JsonBuilder;
import fr.AxelVatan.CMWLink.Common.WebServer.Router.Handler;
import fr.AxelVatan.CMWLink.Common.WebServer.Router.RouteMatcher;
import fr.AxelVatan.CMWLink.Common.WebServer.Router.RoutedHttpRequest;
import fr.AxelVatan.CMWLink.Common.WebServer.Router.RoutedHttpResponse;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.CharsetUtil;

public class DefaultRoutes {
	
	public DefaultRoutes(WebServer ws) {
		RouteMatcher r = ws.getRouter();
		r.noMatch(new Handler<FullHttpResponse, RoutedHttpRequest>() {
			@SuppressWarnings("deprecation")
			@Override
			public FullHttpResponse handle(RoutedHttpRequest event) {
				ws.getConfig().getLog().info("[CMW-Link] [HTTP] 404 " + event.getRequest().getUri());
				return buildResponse(HttpResponseStatus.OK, "text/plain", event.getRequest().getUri() + " wasn't found. This is a Minecraft server. HTTP on this port by JSONAPI. JSONAPI by Alec Gorge.\n");
			}
		});
		
		r.everyMatch(new Handler<Void, RoutedHttpResponse>() {
			
			@SuppressWarnings("deprecation")
			@Override
			public Void handle(RoutedHttpResponse event) {
				ws.getConfig().getLog().info("[CMW-Link] [HTTP] " +
									event.getRes().getStatus().code() + " " +
									event.getRequest().getMethod().toString() + " " +
									event.getRequest().getUri());
				return null;
			}
		});

        r.get("/api/2/call", null);

        r.get("/", new Handler<FullHttpResponse, RoutedHttpRequest>() {
            @Override
            public FullHttpResponse handle(RoutedHttpRequest event) {
            	JsonBuilder json = new JsonBuilder()
    					.append("CODE", 200)
    					.append("NAME", "CraftMyWebSite_Link")
    					.append("VERSION", ws.getConfig().getVersion());
                return buildResponse(HttpResponseStatus.OK, "application/json", json.build());
            }
        });
    }
	
	public static FullHttpResponse buildResponse(HttpResponseStatus resp, String type, String body) {
		ByteBuf buf = Unpooled.copiedBuffer(body, CharsetUtil.UTF_8);
		FullHttpResponse r = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, resp, buf);
		r.headers().set("Access-Control-Allow-Origin", "*");
		r.headers().set("Content-Length", buf.readableBytes());

		return r;
	}
}
