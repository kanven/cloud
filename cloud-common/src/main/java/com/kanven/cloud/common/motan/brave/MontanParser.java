package com.kanven.cloud.common.motan.brave;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.weibo.api.motan.rpc.Request;

import brave.Span;

public class MontanParser {

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

	public void onRequest(Span span, Request request) {
		span.tag("requestId", request.getRequestId() + "");
		span.tag("method",
				request.getInterfaceName() + "." + request.getMethodName() + "(" + request.getParamtersDesc() + ")");
		span.tag("params", join(request.getArguments()));
		span.tag("retries", request.getRetries() + "");
		span.tag("protocol.version", request.getRpcProtocolVersion() + "");
		Map<String, String> attachments = request.getAttachments();
		if (attachments != null && attachments.size() > 0) {
			Set<Entry<String, String>> entries = attachments.entrySet();
			for (Entry<String, String> entry : entries) {
				span.tag("attachment." + entry.getKey(), entry.getValue());
			}
		}
	}

}
