package com.kanven.cloud.common.motan.filter.tracing;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.kanven.cloud.common.brave.BraveClientHandler;
import com.kanven.cloud.common.utils.IPUtil;
import com.weibo.api.motan.rpc.Caller;
import com.weibo.api.motan.rpc.Request;
import com.weibo.api.motan.rpc.Response;
import com.weibo.api.motan.rpc.URL;

import brave.Span;
import brave.Tracer;
import brave.Tracer.SpanInScope;
import brave.Tracing;
import brave.propagation.Propagation.Setter;
import brave.propagation.TraceContext;

class MotanClientInterceptor {

	private final Tracer tracer;

	private final TraceContext.Injector<Map<String, String>> injector;

	private final MotanParser parser = new MotanParser();

	private final BraveClientHandler handler;

	public MotanClientInterceptor(Tracing tracing) {
		this.tracer = tracing.tracer();
		this.injector = tracing.propagation().injector(
				new Setter<Map<String, String>, String>() {
					@Override
					public void put(Map<String, String> carrier, String key,
							String value) {
						carrier.remove(key);
						if (value != null) {
							carrier.put(key, value);
						}
					}
				});
		this.handler = new BraveClientHandler(tracing);
	}

	public void beforHandler(Request request, Caller<?> caller, URL url) {
		String host = url.getHost();
		if (StringUtils.isBlank(host)) {
			host = IPUtil.ip;
		}
		Map<String, String> attachments = request.getAttachments();
		attachments.put("X-Forwarded-For", host + ":" + url.getPort());
		Span span = handler.handleReceive(injector, request.getAttachments(),
				host, url.getPort());
		try (SpanInScope scope = tracer.withSpanInScope(span)) {
			parser.onRequest(span, request, url);
		}
	}

	public void afterCompletion(Response response) {
		handler.handleSend();
	}

}
