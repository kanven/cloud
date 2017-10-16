package com.kanven.cloud.common.motan.brave;

import brave.Tracing;

public class MontanTracingContext {

	private static HandlerInterceptor client = null;

	private static HandlerInterceptor server = null;

	public void setTracing(Tracing tracing) {
		if (tracing != null) {
			MontanTracing montanTracing = MontanTracing.newBuilder(tracing).build();
			client = new MontanClientInterceptor(montanTracing);
			server = new MotanServerInterceptor(montanTracing);
		}
	}

	public static HandlerInterceptor getClientInterceptor() {
		return client;
	}

	public static HandlerInterceptor getServerInterceptor() {
		return server;
	}

}
