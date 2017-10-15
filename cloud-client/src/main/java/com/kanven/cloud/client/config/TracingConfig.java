package com.kanven.cloud.client.config;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import brave.Tracing;
import brave.context.log4j2.ThreadContextCurrentTraceContext;
import brave.http.HttpTracing;
import brave.spring.web.TracingClientHttpRequestInterceptor;
import brave.spring.webmvc.TracingHandlerInterceptor;
import zipkin2.Endpoint;
import zipkin2.Span;
import zipkin2.reporter.AsyncReporter;
import zipkin2.reporter.Sender;
import zipkin2.reporter.okhttp3.OkHttpSender;

@Configuration
@Import({ TracingClientHttpRequestInterceptor.class, TracingHandlerInterceptor.class })
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
	private RestTemplate template;

	@Autowired
	private TracingClientHttpRequestInterceptor client;

	@Autowired
	private TracingHandlerInterceptor server;

	@Bean
	RestTemplate template() {
		return new RestTemplate();
	}

	@PostConstruct
	public void init() {
		List<ClientHttpRequestInterceptor> interceptors = new ArrayList<ClientHttpRequestInterceptor>(
				template.getInterceptors());
		interceptors.add(client);
		template.setInterceptors(interceptors);
	}

	@Bean
	HttpTracing tracing() {
		Sender sender = OkHttpSender.create(url);
		AsyncReporter<Span> reporter = AsyncReporter.create(sender);
		Endpoint endpoint = Endpoint.newBuilder().serviceName(serverName).ip(address).port(port).build();
		Tracing tracing = Tracing.newBuilder().localEndpoint(endpoint).spanReporter(reporter)
				.currentTraceContext(ThreadContextCurrentTraceContext.create()).build();
		return HttpTracing.create(tracing);
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(server);
	}

}
