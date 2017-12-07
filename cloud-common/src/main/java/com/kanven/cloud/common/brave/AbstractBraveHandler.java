package com.kanven.cloud.common.brave;

import brave.Span;
import brave.Tracer;
import brave.Tracer.SpanInScope;
import brave.Tracing;

/**
 * 
 * @author kanven
 * 
 */
abstract class AbstractBraveHandler {

	protected static ThreadLocal<SpanInScope> SCOPE_LOCAL = new ThreadLocal<SpanInScope>();

	protected static ThreadLocal<Span> CLIENT_SPAN_LOCAL = new ThreadLocal<Span>();

	final Tracer tracer;

	AbstractBraveHandler(Tracing tracing) {
		this.tracer = tracing.tracer();
	}

	public void handleSend() {
		Span span = null;
		try {
			span = CLIENT_SPAN_LOCAL.get(); // 客户端
			if (span != null) {
				CLIENT_SPAN_LOCAL.remove();
			} else {
				span = tracer.currentSpan(); // 服务端
				if (span != null) {
					SpanInScope scope = SCOPE_LOCAL.get();
					SCOPE_LOCAL.remove();
					if (scope != null) {
						scope.close();
					}
				}
			}
		} finally {
			if (span != null && !span.isNoop()) {
				span.finish();
			}
		}
	}

}
