package com.kanven.cloud.common.motan.brave;

import brave.Tracing;
import zipkin2.Endpoint;
import zipkin2.reporter.AsyncReporter;
import zipkin2.reporter.okhttp3.OkHttpSender;

public class BraveTracingFactory {

	private String url;

	private String serverName;

	private String host;

	private int port;

	public BraveTracingFactory() {

	}

	public BraveTracingFactory(String url, String serverName) {
		this.url = url;
		this.serverName = serverName;
	}

	public Tracing getTracing() {
		OkHttpSender sender = OkHttpSender.create(url);
		AsyncReporter<zipkin2.Span> reporter = AsyncReporter.create(sender);
//		Endpoint.Builder builder = Endpoint.newBuilder();
//		builder.ip(host).port(port);
		return Tracing.newBuilder().localServiceName(serverName)/*.localEndpoint(builder.build())*/.spanReporter(reporter)
				.build();
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getServerName() {
		return serverName;
	}

	public void setServerName(String serverName) {
		this.serverName = serverName;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

}
