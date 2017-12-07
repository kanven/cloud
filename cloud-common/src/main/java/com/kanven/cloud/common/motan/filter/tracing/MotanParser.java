package com.kanven.cloud.common.motan.filter.tracing;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.kanven.cloud.common.utils.IPUtil;
import com.weibo.api.motan.rpc.Request;
import com.weibo.api.motan.rpc.URL;

import brave.Span;

class MotanParser {

	private <T> String join(T[] arrs) {
		StringBuilder builder = new StringBuilder();
		if (arrs != null) {
			int len = arrs.length;
			builder.append("[");
			for (int i = 0; i < len; i++) {
				builder.append(arrs[i]);
				if (i < len - 1) {
					builder.append(",");
				}
			}
			builder.append("]");
		}
		return builder.toString();
	}

	public void onRequest(Span span, Request request, URL url) {
		span.tag("requestId", request.getRequestId() + "");
		span.tag("method",
				request.getInterfaceName() + "." + request.getMethodName()
						+ "(" + request.getParamtersDesc() + ")");
		span.tag("params", join(request.getArguments()));
		span.tag("retries", request.getRetries() + "");
		span.tag("rpc.protocol", url.getProtocol());
		span.tag("port", url.getPort() + "");
		span.tag("host",
				StringUtils.isBlank(url.getHost()) ? IPUtil.ip : url.getHost());
		span.tag("protocol.version", request.getRpcProtocolVersion() + "");

		Map<String, String> attachments = request.getAttachments();
		if (attachments != null && attachments.size() > 0) {
			Set<Entry<String, String>> entries = attachments.entrySet();
			for (Entry<String, String> entry : entries) {
				String key = entry.getKey();
				switch (key) {
				case "X-B3-Sampled":
				case "X-B3-SpanId":
				case "X-B3-TraceId":
				case "X-B3-ParentSpanId":
				case "X-B3-Flags":
				case "X-Forwarded-For":
					continue;
				default:
					span.tag("attachment." + key, entry.getValue());
				}
			}
		}
	}

}
