package com.kanven.cloud.common.motan.filter.tracing;

import com.weibo.api.motan.core.extension.Scope;
import com.weibo.api.motan.core.extension.Spi;
import com.weibo.api.motan.core.extension.SpiMeta;
import com.weibo.api.motan.filter.Filter;
import com.weibo.api.motan.rpc.Caller;
import com.weibo.api.motan.rpc.Provider;
import com.weibo.api.motan.rpc.Request;
import com.weibo.api.motan.rpc.Response;
import com.weibo.api.motan.rpc.URL;

/**
 * 
 * @author kanven
 * 
 */
@Spi(scope = Scope.SINGLETON)
@SpiMeta(name = "bracing")
public class MotanTracingFilter implements Filter {

	@Override
	public Response filter(Caller<?> caller, Request request) {
		final URL url = caller.getUrl();
		boolean isServer = caller instanceof Provider ? true : false;
		Response response = null;
		if (isServer) { // 服务端
			final MotanServerInterceptor server = MotanTracingContext
					.getInstance().getServer();
			server.beforHandler(request, url);
			response = caller.call(request);
			server.afterCompletion(request, response);
		} else { // 客户端
			final MotanClientInterceptor client = MotanTracingContext
					.getInstance().getClient();
			client.beforHandler(request, caller, url);
			response = caller.call(request);
			client.afterCompletion(response);
		}
		return response;
	}

}
