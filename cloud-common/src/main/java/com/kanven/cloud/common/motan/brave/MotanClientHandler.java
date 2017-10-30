package com.kanven.cloud.common.motan.brave;

import java.util.Map;

import com.weibo.api.motan.rpc.Caller;
import com.weibo.api.motan.rpc.Request;
import com.weibo.api.motan.rpc.URL;

import brave.Span;
import brave.propagation.CurrentTraceContext;
import brave.propagation.SamplingFlags;
import brave.propagation.TraceContext;
import brave.propagation.TraceContext.Injector;
import zipkin2.Endpoint;

class MotanClientHandler extends AbstractMotanHandler {

	final CurrentTraceContext currentTraceContext;

	MotanClientHandler(MotanTracing tracing) {
		super(tracing);
		this.currentTraceContext = tracing.tracing().currentTraceContext();
	}

	public <C> Span handleReceive(Injector<C> injector, C carrier, Request request, Caller<?> caller) {
		TraceContext parent = currentTraceContext.get();
		Span span = parent != null ? tracer.newChild(parent) : tracer.newTrace(SamplingFlags.SAMPLED);
		injector.inject(span.context(), carrier);
		if (span.isNoop()) {
			return span;
		}
		span.kind(Span.Kind.CLIENT);
		URL url = caller.getUrl();
		Endpoint.Builder builder = Endpoint.newBuilder();
		builder.ip(url.getHost()).port(url.getPort());
		Map<String, String> attachments = request.getAttachments();
		attachments.put("X-Forwarded-For", host + ":" + port);
		span.remoteEndpoint(builder.build());
		return span.start();
	}

}
