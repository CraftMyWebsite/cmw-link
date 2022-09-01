package fr.AxelVatan.CMWLink.Common.WebServer;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.json.simpleForBukkit.JSONArray;
import org.json.simpleForBukkit.JSONObject;
import org.json.simpleForBukkit.JSONResponse;
import org.json.simpleForBukkit.parser.JSONParser;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.util.CharsetUtil;

public class APIv2Handler {
	
	private WebServer ws;
	static String CONTENT_TYPE_JSON = "application/json";
	
	FullHttpRequest request;
	QueryStringDecoder uri;
	
	List<JSONResponse> requests = new ArrayList<JSONResponse>();
	
	JSONParser parser = new JSONParser();
	
	@SuppressWarnings("deprecation")
	public APIv2Handler (FullHttpRequest req, WebServer ws) {
		request = req;
		uri = new QueryStringDecoder(request.getUri());
	}
	
	public boolean canServe(QueryStringDecoder u) {
		ws.getConfig().getLog().info("can serve? " + u.path());
		return u.path().startsWith("/api/2/") && !u.path().equals("/api/2/websocket");
	}
	
	public FullHttpResponse serve() {
		try {
			if(uri.path().equals("/api/2/call")) {
				readPayload(false);
				return call();
			}
			else if(uri.path().equals("/api/2/version")) {
				return version();
			}
			else {
				return resp(HttpResponseStatus.NOT_FOUND, "text/plain", "Not found.\n");
			}
		}
		catch (Exception e) {
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			return resp(HttpResponseStatus.BAD_REQUEST, CONTENT_TYPE_JSON, "["+JSONResponse.APIError(errors.toString(), 4, "JSON_PARSE_ERROR", "").toJSONString()+"]\n");
		}
	}
	
	public boolean isStream() {
		return uri.path().equals("/api/2/subscribe");
	}

	public FullHttpResponse call() {
		JSONArray a = new JSONArray();
		
		for(JSONResponse resp : requests) {
			a.add(resp.getJSONObject());
		}
		
		String json = a.toJSONString();
		
		ws.getConfig().getLog().info("returning: " + json);
		return resp(HttpResponseStatus.OK, CONTENT_TYPE_JSON, json + "\n");
	}
	
	public FullHttpResponse version() {
		JSONObject versionObj = new JSONObject();
		versionObj.put("version", "TEST");
		versionObj.put("server_version", "OK");
		
		return resp(HttpResponseStatus.OK, CONTENT_TYPE_JSON, versionObj.toJSONString());
	}
	
	public FullHttpResponse resp(HttpResponseStatus resp, String type, String body) {
		if(uri.parameters().containsKey("callback") && type.equals(CONTENT_TYPE_JSON)) {
			body = uri.parameters().get("callback") + "(" + body + ");";
		}
		
		ByteBuf buf = Unpooled.copiedBuffer(body, CharsetUtil.UTF_8);
		FullHttpResponse r = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, resp, buf);
		r.headers().set("Access-Control-Allow-Origin", "*");
		r.headers().set("Content-Length", buf.readableBytes());
		r.headers().set("Content-Type", type);

		return r;
	}
	
	public void readPayload(boolean stream) throws Exception {
        String json = null;
        if (uri.parameters().containsKey("json")) {
            json = uri.parameters().get("json").get(0);
        } else {
            ByteBuf byteBuf = request.content();
            if (byteBuf.isReadable()) {
                json = byteBuf.toString(Charset.forName("UTF-8"));
            }
        }

        if (json != null) {
            Object o = parser.parse(json);
			
            ws.getConfig().getLog().info("json obj: "+ o);
			
			if(o instanceof JSONObject) {
				requests.add(new JSONResponse((JSONObject)o, stream));
			}
			else if(o instanceof JSONArray) {
				for(Object obj : (JSONArray) o) {
					if(obj instanceof JSONObject) {
						requests.add(new JSONResponse((JSONObject)obj, stream));
					}
				}
			}
		}
	}	

}
