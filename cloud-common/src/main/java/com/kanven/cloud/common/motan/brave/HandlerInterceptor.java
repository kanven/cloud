package com.kanven.cloud.common.motan.brave;

import com.weibo.api.motan.rpc.Request;
import com.weibo.api.motan.rpc.Response;

public interface HandlerInterceptor {

	void beforHandler(Request request);

	void afterCompletion(Request request, Response response);
}
