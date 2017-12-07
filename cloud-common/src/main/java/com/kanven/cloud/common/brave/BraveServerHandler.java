package com.kanven.cloud.common.brave;

import zipkin2.Endpoint;
import brave.Span;
import brave.Tracing;
import brave.propagation.TraceContext;
import brave.propagation.TraceContext.Extractor;
import brave.propagation.TraceContextOrSamplingFlags;

/**
 * 
 * @author 蒋远龙
 * 
 */
public class BraveServerHandler extends AbstractBraveHandler {

	public BraveServerHandler(Tracing tracing) {
		super(tracing);
	}

	private Span nextSpan(final TraceContextOrSamplingFlags contextOrFlags) {
		TraceContext extracted = contextOrFlags.context();
		if (extracted != null) {
			if (extracted.sampled() != null) {
				return tracer.joinSpan(contextOrFlags.context());
			}
			return tracer.joinSpan(contextOrFlags.context());
		}
		return tracer.newTrace(contextOrFlags.samplingFlags());
	}

	public <C> Span handleReceive(Extractor<C> extractor, C carrier,
			String host, int port, String name) {
		Span span = nextSpan(extractor.extract(carrier));
		if (!span.isNoop()) {
			span.kind(Span.Kind.SERVER);
			Endpoint.Builder builder = Endpoint.newBuilder();
			builder.ip(host).port(port);
			span.remoteEndpoint(builder.build());
			span.name(name);
			span.start();
		}
		SCOPE_LOCAL.set(tracer.withSpanInScope(span));
		return span;
	}

}
