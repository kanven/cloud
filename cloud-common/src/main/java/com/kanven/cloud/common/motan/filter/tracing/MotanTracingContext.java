package com.kanven.cloud.common.motan.filter.tracing;

import com.kanven.cloud.common.brave.TracingFactory;

/**
 * 
 * @author 蒋远龙
 * 
 */
class MotanTracingContext {

	private final MotanClientInterceptor client;

	private final MotanServerInterceptor server;

	private MotanTracingContext() {
		server = new MotanServerInterceptor(TracingFactory.getTracing());
		client = new MotanClientInterceptor(TracingFactory.getTracing());
	}

	public static MotanTracingContext getInstance() {
		return MotanTracingContextHolder.INSTANCE;
	}

	private static class MotanTracingContextHolder {
		private final static MotanTracingContext INSTANCE = new MotanTracingContext();
	}

	public MotanServerInterceptor getServer() {
		return server;
	}

	public MotanClientInterceptor getClient() {
		return client;
	}

}
