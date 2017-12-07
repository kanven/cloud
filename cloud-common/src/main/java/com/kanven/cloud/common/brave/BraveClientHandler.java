package com.kanven.cloud.common.brave;

import org.apache.commons.lang3.StringUtils;

import com.kanven.cloud.common.utils.IPUtil;

import brave.Span;
import brave.Tracing;
import brave.propagation.CurrentTraceContext;
import brave.propagation.SamplingFlags;
import brave.propagation.TraceContext;
import brave.propagation.TraceContext.Injector;
import zipkin2.Endpoint;

/**
 * 
 * @author kanven
 *
 */
public class BraveClientHandler extends AbstractBraveHandler {

	private final CurrentTraceContext currentTraceContext;

	public BraveClientHandler(Tracing tracing) {
		super(tracing);
		this.currentTraceContext = tracing.currentTraceContext();
	}

	public <C> Span handleReceive(Injector<C> injector, C carrier, String host,
			int port) {
		TraceContext parent = currentTraceContext.get();
		Span span = parent != null ? tracer.newChild(parent) : tracer
				.newTrace(SamplingFlags.SAMPLED);
		injector.inject(span.context(), carrier);
		if (span.isNoop()) {
			return span;
		}
		span.kind(Span.Kind.CLIENT);
		Endpoint.Builder builder = Endpoint.newBuilder();
		if (StringUtils.isBlank(host)) {
			host = IPUtil.ip;
		}
		int p = port > 0 ? 0 : port;
		builder.ip(host).port(p);
		span.remoteEndpoint(builder.build());
		span.start();
		CLIENT_SPAN_LOCAL.set(span);
		return span;
	}

}
