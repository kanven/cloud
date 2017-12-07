package com.kanven.cloud.common.motan.proxy.spi;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.weibo.api.motan.core.extension.SpiMeta;
import com.weibo.api.motan.proxy.ProxyFactory;

import net.sf.cglib.proxy.Enhancer;

/**
 * 动态代理CGLIB实现
 * 
 * @author 蒋远龙
 * 
 */
@SpiMeta(name = "cglib")
public class CglibProxyFactory implements ProxyFactory {

	private ConcurrentMap<Class<?>, Object> proxies = new ConcurrentHashMap<Class<?>, Object>();

	@Override
	public <T> T getProxy(Class<T> clz, InvocationHandler invocationHandler) {
		Object proxy = proxies.get(clz);
		if (proxy == null) {
			Enhancer enhancer = new Enhancer();
			enhancer.setSuperclass(clz);
			enhancer.setCallback(new InvocationHandlerAdapter(invocationHandler));
			proxies.putIfAbsent(clz, enhancer.create());
			proxy = proxies.get(clz);
		}
		return clz.cast(proxy);
	}

	private static class InvocationHandlerAdapter implements
			net.sf.cglib.proxy.InvocationHandler {

		private final InvocationHandler handler;

		private InvocationHandlerAdapter(InvocationHandler handler) {
			this.handler = handler;
		}

		@Override
		public Object invoke(Object proxy, Method method, Object[] args)
				throws Throwable {
			return handler.invoke(proxy, method, args);
		}

	}

}
