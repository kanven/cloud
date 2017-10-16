package com.kanven.cloud.common.motan.brave;

import com.weibo.api.motan.rpc.Response;

import brave.Span;
import brave.Tracer;

abstract class AbstractMontanHandler {

	final Tracer tracer;

	AbstractMontanHandler(Tracer tracer) {
		this.tracer = tracer;
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
