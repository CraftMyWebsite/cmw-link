package fr.AxelVatan.CMWLink.Common.WebServer.Router;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.QueryStringDecoder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RouteMatcher extends SimpleChannelInboundHandler<FullHttpRequest> {

	private final List<PatternBinding> getBindings = new ArrayList<PatternBinding>();
	private final List<PatternBinding> putBindings = new ArrayList<PatternBinding>();
	private final List<PatternBinding> postBindings	= new ArrayList<PatternBinding>();
	private final List<PatternBinding> deleteBindings = new ArrayList<PatternBinding>();
	private final List<PatternBinding> optionsBindings = new ArrayList<PatternBinding>();
	private final List<PatternBinding> headBindings	= new ArrayList<PatternBinding>();
	private final List<PatternBinding> traceBindings = new ArrayList<PatternBinding>();
	private final List<PatternBinding> connectBindings = new ArrayList<PatternBinding>();
	private final List<PatternBinding> patchBindings = new ArrayList<PatternBinding>();
	private Handler<FullHttpResponse, RoutedHttpRequest> noMatchHandler;
	private Handler<Void, RoutedHttpResponse> everyMatchHandler;

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
		serveRequest(ctx, request);
	}

	List<PatternBinding> getBindingsForRequest(FullHttpRequest request) {
		@SuppressWarnings("deprecation")
		HttpMethod m = request.getMethod();
		if (m.equals(HttpMethod.GET)) {
			return getBindings;
		}
		else if (m.equals(HttpMethod.PUT)) {
			return putBindings;
		}
		else if (m.equals(HttpMethod.POST)) {
			return postBindings;
		}
		else if (m.equals(HttpMethod.DELETE)) {
			return deleteBindings;
		}
		else if (m.equals(HttpMethod.OPTIONS)) {
			return optionsBindings;
		}
		else if (m.equals(HttpMethod.HEAD)) {
			return headBindings;
		}
		else if (m.equals(HttpMethod.TRACE)) {
			return traceBindings;
		}
		else if (m.equals(HttpMethod.PATCH)) {
			return patchBindings;
		}
		else if (m.equals(HttpMethod.CONNECT)) {
			return connectBindings;
		}

		return null;
	}

	@SuppressWarnings("deprecation")
	public boolean serveRequest(ChannelHandlerContext ctx, FullHttpRequest request) {
		if (!request.getDecoderResult().isSuccess()) {
			sendHttpResponse(ctx, request, new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST));
			return false;
		}

		return route(ctx, request, getBindingsForRequest(request));
	}

	public RouteMatcher get(String pattern, Handler<FullHttpResponse, RoutedHttpRequest> handler) {
		addPattern(pattern, handler, getBindings);
		return this;
	}

	public RouteMatcher put(String pattern, Handler<FullHttpResponse, RoutedHttpRequest> handler) {
		addPattern(pattern, handler, putBindings);
		return this;
	}

	public RouteMatcher post(String pattern, Handler<FullHttpResponse, RoutedHttpRequest> handler) {
		addPattern(pattern, handler, postBindings);
		return this;
	}

	public RouteMatcher delete(String pattern, Handler<FullHttpResponse, RoutedHttpRequest> handler) {
		addPattern(pattern, handler, deleteBindings);
		return this;
	}

	public RouteMatcher options(String pattern, Handler<FullHttpResponse, RoutedHttpRequest> handler) {
		addPattern(pattern, handler, optionsBindings);
		return this;
	}

	public RouteMatcher head(String pattern, Handler<FullHttpResponse, RoutedHttpRequest> handler) {
		addPattern(pattern, handler, headBindings);
		return this;
	}

	public RouteMatcher trace(String pattern, Handler<FullHttpResponse, RoutedHttpRequest> handler) {
		addPattern(pattern, handler, traceBindings);
		return this;
	}

	public RouteMatcher connect(String pattern, Handler<FullHttpResponse, RoutedHttpRequest> handler) {
		addPattern(pattern, handler, connectBindings);
		return this;
	}

	public RouteMatcher patch(String pattern, Handler<FullHttpResponse, RoutedHttpRequest> handler) {
		addPattern(pattern, handler, patchBindings);
		return this;
	}

	public RouteMatcher all(String pattern, Handler<FullHttpResponse, RoutedHttpRequest> handler) {
		addPattern(pattern, handler, getBindings);
		addPattern(pattern, handler, putBindings);
		addPattern(pattern, handler, postBindings);
		addPattern(pattern, handler, deleteBindings);
		addPattern(pattern, handler, optionsBindings);
		addPattern(pattern, handler, headBindings);
		addPattern(pattern, handler, traceBindings);
		addPattern(pattern, handler, connectBindings);
		addPattern(pattern, handler, patchBindings);
		return this;
	}

	public RouteMatcher getWithRegEx(String regex, Handler<FullHttpResponse, RoutedHttpRequest> handler) {
		addRegEx(regex, handler, getBindings);
		return this;
	}

	public RouteMatcher putWithRegEx(String regex, Handler<FullHttpResponse, RoutedHttpRequest> handler) {
		addRegEx(regex, handler, putBindings);
		return this;
	}

	public RouteMatcher postWithRegEx(String regex, Handler<FullHttpResponse, RoutedHttpRequest> handler) {
		addRegEx(regex, handler, postBindings);
		return this;
	}

	public RouteMatcher deleteWithRegEx(String regex, Handler<FullHttpResponse, RoutedHttpRequest> handler) {
		addRegEx(regex, handler, deleteBindings);
		return this;
	}

	public RouteMatcher optionsWithRegEx(String regex, Handler<FullHttpResponse, RoutedHttpRequest> handler) {
		addRegEx(regex, handler, optionsBindings);
		return this;
	}

	public RouteMatcher headWithRegEx(String regex, Handler<FullHttpResponse, RoutedHttpRequest> handler) {
		addRegEx(regex, handler, headBindings);
		return this;
	}

	public RouteMatcher traceWithRegEx(String regex, Handler<FullHttpResponse, RoutedHttpRequest> handler) {
		addRegEx(regex, handler, traceBindings);
		return this;
	}

	public RouteMatcher connectWithRegEx(String regex, Handler<FullHttpResponse, RoutedHttpRequest> handler) {
		addRegEx(regex, handler, connectBindings);
		return this;
	}

	public RouteMatcher patchWithRegEx(String regex, Handler<FullHttpResponse, RoutedHttpRequest> handler) {
		addRegEx(regex, handler, patchBindings);
		return this;
	}

	public RouteMatcher allWithRegEx(String regex, Handler<FullHttpResponse, RoutedHttpRequest> handler) {
		addRegEx(regex, handler, getBindings);
		addRegEx(regex, handler, putBindings);
		addRegEx(regex, handler, postBindings);
		addRegEx(regex, handler, deleteBindings);
		addRegEx(regex, handler, optionsBindings);
		addRegEx(regex, handler, headBindings);
		addRegEx(regex, handler, traceBindings);
		addRegEx(regex, handler, connectBindings);
		addRegEx(regex, handler, patchBindings);
		return this;
	}

	public RouteMatcher noMatch(Handler<FullHttpResponse, RoutedHttpRequest> handler) {
		noMatchHandler = handler;
		return this;
	}

	public RouteMatcher everyMatch(Handler<Void, RoutedHttpResponse> handler) {
		everyMatchHandler = handler;
		return this;
	}

	private static void addPattern(String input, Handler<FullHttpResponse, RoutedHttpRequest> handler, List<PatternBinding> bindings) {
		Matcher m = Pattern.compile(":([A-Za-z][A-Za-z0-9_]*)").matcher(input);
		StringBuffer sb = new StringBuffer();
		Set<String> groups = new HashSet<String>();
		while (m.find()) {
			String group = m.group().substring(1);
			if (groups.contains(group)) {
				throw new IllegalArgumentException("Cannot use identifier " + group + " more than once in pattern string");
			}
			m.appendReplacement(sb, "(?<$1>[^\\/]+)");
			groups.add(group);
		}
		m.appendTail(sb);
		String regex = sb.toString();
		PatternBinding binding = new PatternBinding(NamedPattern.compile(regex), groups, handler);
		bindings.add(binding);
	}

	private static void addRegEx(String input, Handler<FullHttpResponse, RoutedHttpRequest> handler, List<PatternBinding> bindings) {
		PatternBinding binding = new PatternBinding(NamedPattern.compile(input), null, handler);
		bindings.add(binding);
	}

	@SuppressWarnings("deprecation")
	public FullHttpResponse getResponse(ChannelHandlerContext ctx, FullHttpRequest request) {
		FullHttpResponse resp = null;
		if (!request.getDecoderResult().isSuccess()) {
			resp = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST);
			sendHttpResponse(ctx, request, resp);
		}
		else {
			resp = getResponseForRoute(ctx, request, getBindingsForRequest(request));
		}

		return resp;
	}

	FullHttpResponse getResponseForRoute(ChannelHandlerContext ctx, FullHttpRequest request, List<PatternBinding> bindings) {
		RoutedHttpRequest rreq = new RoutedHttpRequest(ctx, request);
		for (PatternBinding binding : bindings) {
			@SuppressWarnings("deprecation")
			QueryStringDecoder uri = new QueryStringDecoder(request.getUri());
			NamedMatcher m = binding.pattern.matcher(uri.path());
			if (m.matches()) {
				Map<String, List<String>> params = new HashMap<String, List<String>>(m.groupCount());
				if (binding.paramNames != null) {
					for (String param : binding.paramNames) {
						List<String> l = new ArrayList<String>();
						l.add(m.group(param));
						params.put(param, l);
					}
				}
				else {
					for (int i = 0; i < m.groupCount(); i++) {
						List<String> l = new ArrayList<String>();
						l.add(m.group(i + 1));
						params.put("param" + i, l);
					}
				}
				uri.parameters().putAll(params);
				FullHttpResponse res = binding.handler.handle(rreq);

				return res;
			}
		}
		if (noMatchHandler != null) {
			return noMatchHandler.handle(rreq);
		}
		return new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.NOT_FOUND);
	}

	private boolean route(ChannelHandlerContext ctx, FullHttpRequest request, List<PatternBinding> bindings) {
		FullHttpResponse res = getResponseForRoute(ctx, request, bindings);
		sendHttpResponse(ctx, request, res);
		if (everyMatchHandler != null) {
			everyMatchHandler.handle(new RoutedHttpResponse(res, request));
		}
		return noMatchHandler != null;
	}

	@SuppressWarnings("deprecation")
	void sendHttpResponse(ChannelHandlerContext ctx, FullHttpRequest req, FullHttpResponse res) {
		if (res == null) {
			return;
		}
		ChannelFuture f = ctx.channel().writeAndFlush(res);
		if (!HttpHeaders.isKeepAlive(req) || res.getStatus().code() != 200) {
			f.addListener(ChannelFutureListener.CLOSE);
		}
	}

	private static class PatternBinding {
		final NamedPattern pattern;
		final Handler<FullHttpResponse, RoutedHttpRequest> handler;
		final Set<String> paramNames;

		private PatternBinding(NamedPattern pattern, Set<String> paramNames, Handler<FullHttpResponse, RoutedHttpRequest> handler) {
			this.pattern = pattern;
			this.paramNames = paramNames;
			this.handler = handler;
		}
	}

}
