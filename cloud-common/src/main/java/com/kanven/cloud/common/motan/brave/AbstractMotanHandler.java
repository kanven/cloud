package com.kanven.cloud.common.motan.brave;

import com.weibo.api.motan.rpc.Response;

import brave.Span;
import brave.Tracer;

abstract class AbstractMotanHandler {

	final Tracer tracer;

	final String serverName;

	final String host;

	final int port;

	AbstractMotanHandler(MotanTracing tracing) {
		this.tracer = tracing.tracing().tracer();
		this.serverName = tracing.serverName();
		this.host = tracing.host();
		this.port = tracing.port();
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
