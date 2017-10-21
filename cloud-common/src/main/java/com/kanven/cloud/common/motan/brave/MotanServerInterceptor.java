package com.kanven.cloud.common.motan.brave;

import java.util.Map;

import com.weibo.api.motan.rpc.Caller;
import com.weibo.api.motan.rpc.Request;
import com.weibo.api.motan.rpc.Response;
import com.weibo.api.motan.rpc.URL;

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
class MotanServerInterceptor {

	private static ThreadLocal<SpanInScope> local = new ThreadLocal<SpanInScope>();

	final Tracer tracer;

	final Extractor<Map<String, String>> extractor;

	final MotanServerHandler handler;

	final MotanParser parser = new MotanParser();

	
	MotanServerInterceptor(MotanTracing tracing) {
		this.tracer = tracing.tracing().tracer();
		this.extractor = tracing.tracing().propagation().extractor(new Getter<Map<String, String>, String>() {
			@Override
			public String get(Map<String, String> carrier, String key) {
				return carrier.get(key);
			}
		});
		handler = new MotanServerHandler(tracing);
	}

	public void beforHandler(final Request request, Caller<?> caller, final URL url) {
		Map<String, String> attachements = request.getAttachments();
		Span span = handler.handleReceive(extractor, attachements, request);
		parser.onRequest(span, request, url);
		local.set(tracer.withSpanInScope(span));
	}

	public void afterCompletion(final Request request, final Response response) {
		try {
			Span span = tracer.currentSpan();
			if (span == null) {
				return;
			}
			SpanInScope scope = local.get();
			handler.handleSend(response, response.getException(), span);
			scope.close();
		} finally {
			local.remove();
		}
	}

}
