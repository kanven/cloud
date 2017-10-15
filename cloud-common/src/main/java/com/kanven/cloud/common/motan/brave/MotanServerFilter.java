package com.kanven.cloud.common.motan.brave;

import java.util.Map;

import com.weibo.api.motan.filter.Filter;
import com.weibo.api.motan.rpc.Caller;
import com.weibo.api.motan.rpc.Request;
import com.weibo.api.motan.rpc.Response;

import brave.Span;
import brave.Tracer;
import brave.propagation.Propagation.Getter;
import brave.propagation.TraceContext.Extractor;

/**
 * 
 * @author kanven
 *
 */
public class MotanServerFilter implements Filter {

	final Tracer tracer;

	final Extractor<Map<String, String>> extractor;

	final MontanServerHandler handler;

	MotanServerFilter(MontanTracing tracing) {
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
	public Response filter(Caller<?> caller, Request request) {
		Span span = handler.handleReceive(extractor, request.getAttachments(), request);
		Response response = null;
		try {
			response = caller.call(request);
		} finally {
			handler.handleSend(response, response.getException(), span);
		}
		return response;
	}

}
