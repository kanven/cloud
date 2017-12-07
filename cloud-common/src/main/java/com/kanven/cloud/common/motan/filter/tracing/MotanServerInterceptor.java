package com.kanven.cloud.common.motan.filter.tracing;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kanven.cloud.common.brave.BraveServerHandler;
import com.weibo.api.motan.rpc.Request;
import com.weibo.api.motan.rpc.Response;
import com.weibo.api.motan.rpc.URL;

import brave.Tracing;
import brave.propagation.Propagation.Getter;
import brave.propagation.TraceContext.Extractor;

/**
 * 
 * @author kanven
 * 
 */
class MotanServerInterceptor {

	private static final Logger log = LoggerFactory
			.getLogger(MotanServerInterceptor.class);

	final Extractor<Map<String, String>> extractor;

	final BraveServerHandler handler;

	MotanServerInterceptor(Tracing tracing) {
		this.extractor = tracing.propagation().extractor(
				new Getter<Map<String, String>, String>() {
					@Override
					public String get(Map<String, String> carrier, String key) {
						return carrier.get(key);
					}
				});
		handler = new BraveServerHandler(tracing);
	}

	public void beforHandler(final Request request, final URL url) {
		Map<String, String> attachements = request.getAttachments();
		String forwarded = attachements.remove("X-Forwarded-For");
		String host = "";
		int port = 0;
		if (StringUtils.isNotBlank(forwarded)) {
			String[] items = forwarded.split(":");
			if (items != null && items.length == 2) {
				try {
					host = items[0];
					port = Integer.parseInt(items[1]);
				} catch (Exception e) {
					log.error("X-Forwarded-For 解析异常", e);
				}
			}
		}
		handler.handleReceive(extractor, attachements, host, port,
				fetchClassName(request) + "." + request.getMethodName());
	}

	private String fetchClassName(Request request) {
		String name = request.getInterfaceName();
		int index = name.lastIndexOf(".");
		return name.substring(index + 1);
	}

	public void afterCompletion(final Request request, final Response response) {
		handler.handleSend();
	}

}
