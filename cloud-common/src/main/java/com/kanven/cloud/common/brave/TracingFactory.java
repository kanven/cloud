package com.kanven.cloud.common.brave;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import zipkin2.reporter.AsyncReporter;
import zipkin2.reporter.kafka11.KafkaSender;
import brave.Tracing;
import brave.Tracing.Builder;
import brave.context.log4j2.ThreadContextCurrentTraceContext;
import brave.propagation.StrictCurrentTraceContext;
import brave.sampler.Sampler;

/**
 * 
 * @author 蒋远龙
 * 
 */
public class TracingFactory {

	private static final Logger log = LoggerFactory
			.getLogger(TracingFactory.class);

	private static Tracing tracing;

	static {
		Tracing tracing = Tracing.newBuilder().build();
		tracing.setNoop(true);
		TracingFactory.tracing = tracing;
	}

	public static Tracing getTracing() {
		if (tracing.isNoop()) {
			log.warn("the tracing will use noop!");
		}
		return tracing;
	}

	public void setConfig(TracingConfig config) {
		if (config != null) {
			String url = config.getUrl();
			if (StringUtils.isNotBlank(url)) {
				Builder builder = Tracing.newBuilder();
				KafkaSender sender = KafkaSender.create(url);
				AsyncReporter<zipkin2.Span> reporter = AsyncReporter
						.create(sender);
				builder.spanReporter(reporter);
				try {
					String serverName = config.getServerName();
					if (StringUtils.isNotBlank(serverName)) {
						builder.localServiceName(serverName);
					}
					TracingFactory.tracing = builder
							.currentTraceContext(
									ThreadContextCurrentTraceContext
											.create(new StrictCurrentTraceContext()))
							.sampler(Sampler.ALWAYS_SAMPLE).build();
				} catch (Exception e) {
					log.error("create tracing failure and will use noop!", e);
				}
			}
		}
	}

}
