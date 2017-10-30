package com.kanven.cloud.common.motan.brave;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.weibo.api.motan.rpc.Request;

import brave.Span;
import brave.propagation.TraceContext;
import brave.propagation.TraceContext.Extractor;
import brave.propagation.TraceContextOrSamplingFlags;
import zipkin2.Endpoint;

class MotanServerHandler extends AbstractMotanHandler {

	private static final Logger log = LoggerFactory.getLogger(MotanServerHandler.class);

	MotanServerHandler(MotanTracing tracing) {
		super(tracing);
	}

	private Span nextSpan(final TraceContextOrSamplingFlags contextOrFlags, final Request request) {
		TraceContext extracted = contextOrFlags.context();
		if (extracted != null) {
			if (extracted.sampled() != null) {
				return tracer.joinSpan(contextOrFlags.context());
			}
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
		Endpoint.Builder builder = Endpoint.newBuilder();
		Map<String, String> attachments = request.getAttachments();
		String forwarded = attachments.remove("X-Forwarded-For");
		if (forwarded != null && !"".equals(forwarded)) {
			String[] items = forwarded.split(":");
			if (items != null && items.length == 2) {
				try {
					builder.ip(items[0]).port(Integer.parseInt(items[1]));
				} catch (Exception e) {
					log.error("X-Forwarded-For 解析异常", e);
				}
			}
		}
		span.remoteEndpoint(builder.build());
		span.name(request.getInterfaceName() + "." + request.getMethodName());
		return span.start();
	}

}
