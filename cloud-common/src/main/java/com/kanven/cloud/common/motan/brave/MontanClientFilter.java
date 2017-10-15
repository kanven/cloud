package com.kanven.cloud.common.motan.brave;

import com.weibo.api.motan.filter.Filter;
import com.weibo.api.motan.rpc.Caller;
import com.weibo.api.motan.rpc.Request;
import com.weibo.api.motan.rpc.Response;

public class MontanClientFilter implements Filter {

	@Override
	public Response filter(Caller<?> caller, Request request) {
		Response response = caller.call(request);
		return response;
	}

}
