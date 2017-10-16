package com.kanven.cloud.common.motan.brave;

import com.weibo.api.motan.rpc.Request;

import brave.Span;
import brave.Tracer;
import brave.propagation.TraceContext.Injector;
import zipkin2.Endpoint;

class MontanClientHandler extends AbstractMontanHandler {

	MontanClientHandler(Tracer tracer) {
		super(tracer);
	}

	public <C>  Span handleReceive(Injector<C> injector, C carrier, Request request) {
		Span span = tracer.nextSpan();
		injector.inject(span.context(), carrier);
		if (span.isNoop()) {
			return span;
		}
		span.kind(Span.Kind.CLIENT);
		Endpoint.Builder remoteEndpoint = Endpoint.newBuilder(); // 需要加强
		span.remoteEndpoint(remoteEndpoint.build());
		return span.start();
	}

}
