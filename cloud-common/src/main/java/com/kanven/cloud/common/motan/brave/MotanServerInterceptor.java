package com.kanven.cloud.common.motan.brave;

import java.util.Map;

import com.weibo.api.motan.rpc.Request;
import com.weibo.api.motan.rpc.Response;

import brave.Span;
import brave.Tracer;
import brave.Tracer.SpanInScope;
import brave.propagation.Propagation.Getter;
import brave.propagation.TraceContext.Extractor;

/**
 * 
 * @author kanven
 *
 */
class MotanServerInterceptor implements HandlerInterceptor {

	final Tracer tracer;

	final Extractor<Map<String, String>> extractor;

	final MontanServerHandler handler;

	final MontanParser parser = new MontanParser();

	MotanServerInterceptor(MontanTracing tracing) {
		this.tracer = tracing.tracing().tracer();
		this.extractor = tracing.tracing().propagation().extractor(new Getter<Map<String, String>, String>() {
			@Override
			public String get(Map<String, String> carrier, String key) {
				return carrier.get(key);
			}
		});
		handler = new MontanServerHandler(tracer);
	}

	@Override
	public void beforHandler(Request request) {
		Span span = handler.handleReceive(extractor, request.getAttachments(), request);
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
