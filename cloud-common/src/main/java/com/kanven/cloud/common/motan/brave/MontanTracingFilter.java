package com.kanven.cloud.common.motan.brave;

import com.weibo.api.motan.core.extension.Scope;
import com.weibo.api.motan.core.extension.Spi;
import com.weibo.api.motan.core.extension.SpiMeta;
import com.weibo.api.motan.filter.Filter;
import com.weibo.api.motan.rpc.Caller;
import com.weibo.api.motan.rpc.Provider;
import com.weibo.api.motan.rpc.Request;
import com.weibo.api.motan.rpc.Response;

/**
 * 
 * @author kanven
 *
 */
@Spi(scope = Scope.SINGLETON)
@SpiMeta(name = "bracing")
public class MontanTracingFilter implements Filter {

	@Override
	public Response filter(Caller<?> caller, Request request) {
		final HandlerInterceptor client = MontanTracingContext.getClientInterceptor();
		final HandlerInterceptor server = MontanTracingContext.getServerInterceptor();
		if (client == null || server == null) {
			return caller.call(request);
		}
		boolean isServer = caller instanceof Provider ? true : false;
		Response response = null;
		if (isServer) { // 服务端
			server.beforHandler(request);
			response = caller.call(request);
			server.afterCompletion(request, response);
		} else { // 客户端
			client.beforHandler(request);
			response = caller.call(request);
			client.afterCompletion(request, response);
		}
		return response;
	}

}
