package com.kanven.cloud.common.motan.brave;

import java.util.Map;

import com.weibo.api.motan.rpc.Caller;
import com.weibo.api.motan.rpc.Request;

import brave.Span;
import brave.propagation.TraceContext.Injector;
import zipkin2.Endpoint;

class MotanClientHandler extends AbstractMotanHandler {

	MotanClientHandler(MotanTracing tracing) {
		super(tracing);
	}

	public <C> Span handleReceive(Injector<C> injector, C carrier, Request request, Caller<?> caller) {
		Span span = tracer.nextSpan();
		injector.inject(span.context(), carrier);
		if (span.isNoop()) {
			return span;
		}
		span.kind(Span.Kind.CLIENT);
		Endpoint.Builder builder = Endpoint.newBuilder();
		builder.serviceName(serverName);
		Map<String, String> attachments = request.getAttachments();
		attachments.put("X-Forwarded-For", host + ":" + port);
		span.remoteEndpoint(builder.build());
		return span.start();
	}

}
