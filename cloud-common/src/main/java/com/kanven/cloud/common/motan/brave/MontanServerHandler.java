package com.kanven.cloud.common.motan.brave;

import com.weibo.api.motan.rpc.Request;

import brave.Span;
import brave.Tracer;
import brave.propagation.TraceContext;
import brave.propagation.TraceContext.Extractor;
import brave.propagation.TraceContextOrSamplingFlags;
import zipkin2.Endpoint;

class MontanServerHandler extends AbstractMontanHandler {

	MontanServerHandler(Tracer tracer) {
		super(tracer);
	}

	private Span nextSpan(final TraceContextOrSamplingFlags contextOrFlags, final Request request) {
		TraceContext extracted = contextOrFlags.context();
		if (extracted != null) {
			if (extracted.sampled() != null)
				return tracer.joinSpan(contextOrFlags.context());
			return tracer.joinSpan(contextOrFlags.context());
		}
		return tracer.newTrace(contextOrFlags.samplingFlags());
	}

	public <C> Span handleReceive(Extractor<C> extractor, C carrier, Request request) {
		Span span = nextSpan(extractor.extract(carrier), request);
		if (span.isNoop()) {
			return span;
		}
		span.kind(Span.Kind.SERVER);
		Endpoint.Builder remoteEndpoint = Endpoint.newBuilder(); // 需要加强
		span.remoteEndpoint(remoteEndpoint.build());
		return span.start();
	}

}
