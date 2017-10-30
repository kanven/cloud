package com.kanven.cloud.common.motan.brave;

import java.util.Map;

import com.weibo.api.motan.rpc.Caller;
import com.weibo.api.motan.rpc.Request;
import com.weibo.api.motan.rpc.Response;
import com.weibo.api.motan.rpc.URL;

import brave.Span;
import brave.Tracer;
import brave.Tracer.SpanInScope;
import brave.propagation.Propagation.Setter;
import brave.propagation.TraceContext;

class MotanClientInterceptor {

	private final Tracer tracer;

	private final TraceContext.Injector<Map<String, String>> injector;

	private final MotanParser parser = new MotanParser();

	private final MotanClientHandler handler;

	private Span span;

	public MotanClientInterceptor(MotanTracing tracing) {
		this.tracer = tracing.tracing().tracer();
		this.injector = tracing.tracing().propagation().injector(new Setter<Map<String, String>, String>() {
			@Override
			public void put(Map<String, String> carrier, String key, String value) {
				carrier.remove(key);
				if (value != null) {
					carrier.put(key, value);
				} 
			}
		});
		this.handler = new MotanClientHandler(tracing);
	}

	public void beforHandler(Request request, Caller<?> caller, URL url) {
		this.span = handler.handleReceive(injector, request.getAttachments(), request, caller);
		try (SpanInScope scope = tracer.withSpanInScope(span)) {
			parser.onRequest(span, request, url);
		} finally {
		}
	}

	public void afterCompletion(Response response) {
		handler.handleSend(response, response.getException(), span);
	}

}
