package com.kanven.cloud.common.motan.brave;

import com.weibo.api.motan.rpc.Request;
import com.weibo.api.motan.rpc.Response;

import brave.Span;
import brave.Tracer;
import brave.propagation.TraceContext;
import brave.propagation.TraceContextOrSamplingFlags;
import zipkin2.Endpoint;

public class MontanServerHandler {

	final Tracer tracer;

	MontanServerHandler(Tracer tracer) {
		this.tracer = tracer;
	}

	public <C> Span handleReceive(final TraceContext.Extractor<C> extractor, final C carrier, final Request request) {
		Span span = nextSpan(extractor.extract(carrier), request);
		if (span.isNoop()) {
			return span;
		}
		span.kind(Span.Kind.SERVER);
		Endpoint.Builder remoteEndpoint = Endpoint.newBuilder(); // 需要加强
		span.remoteEndpoint(remoteEndpoint.build());
		return span.start();
	}

	Span nextSpan(final TraceContextOrSamplingFlags contextOrFlags, final Request request) {
		TraceContext extracted = contextOrFlags.context();
		if (extracted != null) {
			if (extracted.sampled() != null)
				return tracer.joinSpan(contextOrFlags.context());
			return tracer.joinSpan(contextOrFlags.context());
		}
		return tracer.newTrace(contextOrFlags.samplingFlags());
	}

	public void handleSend(final Response response, final Throwable error, final Span span) {
		if (span.isNoop())
			return;
		try {
			// TODO
		} finally {
			span.finish();
		}
	}

}
