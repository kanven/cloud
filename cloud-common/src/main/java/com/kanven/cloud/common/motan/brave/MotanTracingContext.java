package com.kanven.cloud.common.motan.brave;

public class MotanTracingContext {

	private static MotanTracing motanTracing = null;

	private static MotanClientInterceptor client = null;

	private static MotanServerInterceptor server = null;

	public void setTracingFactory(BraveTracingFactory factory) {
		motanTracing = MotanTracing.newBuilder(factory.getTracing()).serverName(factory.getServerName())
				.host(factory.getHost()).port(factory.getPort()).build();
		client = motanTracing.createClientInterceptor();
		server = motanTracing.createServerInterceptor();
	}

	public static MotanTracing getTracing() {
		return motanTracing;
	}

	public static MotanServerInterceptor getServer() {
		return server;
	}

	public static MotanClientInterceptor getClient() {
		return client;
	}

}
