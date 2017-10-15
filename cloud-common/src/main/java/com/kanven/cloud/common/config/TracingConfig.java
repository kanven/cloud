package com.kanven.cloud.common.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.AsyncHandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import brave.Tracing;
import brave.context.log4j2.ThreadContextCurrentTraceContext;
import brave.http.HttpTracing;
import brave.spring.webmvc.TracingHandlerInterceptor;
import zipkin2.Endpoint;
import zipkin2.Span;
import zipkin2.reporter.AsyncReporter;
import zipkin2.reporter.Sender;
import zipkin2.reporter.okhttp3.OkHttpSender;

@Configuration
public class TracingConfig extends WebMvcConfigurerAdapter {

	@Value("${brave.url}")
	private String url;

	@Value("${app.name}")
	private String serverName;

	@Value("${server.address}")
	private String address;

	@Value("${server.port}")
	private int port;

	@Autowired
	private AsyncHandlerInterceptor tracingInterceptor;

	@Bean
	AsyncHandlerInterceptor tracingInterceptor() {
		Sender sender = OkHttpSender.create(url);
		AsyncReporter<Span> reporter = AsyncReporter.create(sender);
		Endpoint endpoint = Endpoint.newBuilder().serviceName(serverName).ip(address).port(port).build();
		Tracing tracing = Tracing.newBuilder().localEndpoint(endpoint).spanReporter(reporter)
				.currentTraceContext(ThreadContextCurrentTraceContext.create()).build();
		return TracingHandlerInterceptor.create(HttpTracing.create(tracing));
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(tracingInterceptor);
	}

}
