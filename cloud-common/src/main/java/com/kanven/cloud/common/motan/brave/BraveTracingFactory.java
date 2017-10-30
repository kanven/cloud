package com.kanven.cloud.common.motan.brave;

import brave.Tracing;
import brave.context.log4j2.ThreadContextCurrentTraceContext;
import brave.propagation.StrictCurrentTraceContext;
import brave.sampler.Sampler;
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
		return Tracing.newBuilder().localServiceName(serverName).spanReporter(reporter)
				.currentTraceContext(ThreadContextCurrentTraceContext.create())
				/*.currentTraceContext(ThreadContextCurrentTraceContext.create(new StrictCurrentTraceContext()))*/
				.sampler(Sampler.ALWAYS_SAMPLE).build();
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
