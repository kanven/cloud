package com.kanven.cloud.common.motan.brave;

import java.util.Map;

import com.weibo.api.motan.rpc.Request;
import com.weibo.api.motan.rpc.Response;

import brave.Span;
import brave.Tracer;
import brave.Tracer.SpanInScope;
import brave.propagation.Propagation.Setter;
import brave.propagation.TraceContext;

class MontanClientInterceptor implements HandlerInterceptor {

	private final Tracer tracer;

	private final TraceContext.Injector<Map<String, String>> injector;

	private final MontanParser parser = new MontanParser();

	private final MontanClientHandler handler;

	public MontanClientInterceptor(MontanTracing tracing) {
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
		this.handler = new MontanClientHandler(this.tracer);
	}

	@Override
	public void beforHandler(Request request) {
		Span span = handler.handleReceive(injector, request.getAttachments(), request);
		try (SpanInScope scope = tracer.withSpanInScope(span)) {
			parser.onRequest(span, request);
		} finally {
		}
	}

	@Override
	public void afterCompletion(Request request, Response response) {
		Span span = tracer.currentSpan();
		if (span == null) {
			return;
		}
		handler.handleSend(response, response.getException(), span);
	}

}
