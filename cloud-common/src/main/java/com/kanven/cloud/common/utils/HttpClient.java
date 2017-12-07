package com.kanven.cloud.common.utils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.kanven.cloud.common.brave.TracingFactory;

import brave.httpasyncclient.TracingHttpAsyncClientBuilder;
import brave.httpclient.TracingHttpClientBuilder;

/**
 * HTTP工具类
 * 
 * @author 蒋远龙
 * 
 */
public class HttpClient {

	private static final Logger log = LoggerFactory.getLogger(HttpClient.class);

	private static final String ENCODE = "UTF-8";

	private static final int SUCCESS = 200;

	private CloseableHttpAsyncClient asyncClient = null;

	private CloseableHttpClient syncClient = null;

	private int connTimeOut;

	private int socketTimeout;

	public HttpClient() {
		this(3000, 5000);
	}

	public HttpClient(int connTimeOut, int socketTimeout) {
		this.connTimeOut = connTimeOut;
		this.socketTimeout = socketTimeout;
		init();
	}

	public void asyncPost(String url, Map<String, String> params,
			FutureCallback<HttpResponse> callback) {
		try {
			HttpPost post = new HttpPost(url);
			List<NameValuePair> pairs = new ArrayList<NameValuePair>();
			Set<String> keys = params.keySet();
			for (String key : keys) {
				pairs.add(new BasicNameValuePair(key, params.get(key)));
			}
			post.setEntity(new UrlEncodedFormEntity(pairs, ENCODE));
			asyncClient.execute(post, callback);
		} catch (Exception e) {
			log.error(String.format("请求(%s)处理出现异常，参数为:%s", url, params));
		}
	}

	public static String parseParameter(Map<String, String> params) {
		List<NameValuePair> pairs = new ArrayList<NameValuePair>(params.size());
		for (Map.Entry<String, String> entry : params.entrySet()) {
			String value = entry.getValue();
			if (value != null) {
				pairs.add(new BasicNameValuePair(entry.getKey(), value));
			}
		}
		try {
			return EntityUtils
					.toString(new UrlEncodedFormEntity(pairs, ENCODE));
		} catch (IOException e) {
			log.error(e.getMessage());
		}
		return "";
	}

	public String doGet(String url) {
		if (StringUtils.isEmpty(url)) {
			log.error("没有指定url地址！");
			return null;
		}
		HttpGet httpGet = new HttpGet(url);
		CloseableHttpResponse response = null;
		String result = null;
		try {
			response = syncClient.execute(httpGet);
			int status = response.getStatusLine().getStatusCode();
			if (status != SUCCESS) {
				log.error(url + "请求失败,状态码:" + status);
			} else {
				result = handlResponse(response);
			}
		} catch (IOException e) {
			log.error(String.format("请求(%s)处理出现异常！", url), e);
		}
		return result;
	}

	public String doPost(String url, Map<String, String> params)
			throws UnsupportedEncodingException {
		if (StringUtils.isEmpty(url)) {
			log.error("没有指定url地址！");
			return null;
		}
		HttpPost post = new HttpPost(url);
		if (params == null || params.isEmpty()) {
			return null;
		}
		List<NameValuePair> pairs = new ArrayList<NameValuePair>();
		Set<String> keys = params.keySet();
		for (String key : keys) {
			pairs.add(new BasicNameValuePair(key, params.get(key)));
		}
		post.setEntity(new UrlEncodedFormEntity(pairs, ENCODE));
		CloseableHttpResponse response = null;
		String result = null;
		try {
			response = syncClient.execute(post);
			int status = response.getStatusLine().getStatusCode();
			if (status != SUCCESS) {
				log.error(String.format("请求(%s)处理失败,状态码:%s,参数:%s", url, status,
						params));
			} else {
				result = handlResponse(response);
			}
		} catch (IOException e) {
			log.error(String.format("请求(%s)处理出现异常,参数：%s", url, params), e);
		}
		return result;
	}

	public <E> String doPost(String url, E entity) {
		if (StringUtils.isEmpty(url)) {
			log.error("没有指定url地址！");
			return null;
		}
		HttpPost post = new HttpPost(url);
		EntityBuilder builder = EntityBuilder.create();
		builder.setText(JSON.toJSONString(entity)).setContentEncoding(ENCODE);
		post.setEntity(builder.build());
		CloseableHttpResponse response = null;
		String result = null;
		try {
			response = syncClient.execute(post);
			int status = response.getStatusLine().getStatusCode();
			if (status != SUCCESS) {
				log.error(String.format("请求(%s)处理失败,状态码:%s,参数:%s", url, status,
						JSON.toJSONString(entity)));
			} else {
				result = handlResponse(response);
			}
		} catch (IOException e) {
			log.error(
					String.format("请求(%s)处理出现异常,参数：%s", url,
							JSON.toJSONString(entity)), e);
		}
		return result;
	}

	public String handlResponse(HttpResponse response) throws ParseException,
			IOException {
		String result = null;
		HttpEntity entity = response.getEntity();
		if (entity != null) {
			result = EntityUtils.toString(entity, ENCODE);
			EntityUtils.consume(entity);
		}
		return result;
	}

	private void init() {
		RequestConfig config = RequestConfig.custom()
				.setConnectTimeout(connTimeOut).setSocketTimeout(socketTimeout)
				.setConnectionRequestTimeout(connTimeOut).build();
		asyncClient = TracingHttpAsyncClientBuilder
				.create(TracingFactory.getTracing())
				.setDefaultRequestConfig(config).build();
		asyncClient.start();
		syncClient = TracingHttpClientBuilder
				.create(TracingFactory.getTracing())
				.setDefaultRequestConfig(config).build();
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				if (asyncClient != null && asyncClient.isRunning()) {
					try {
						asyncClient.close();
					} catch (IOException e) {
						log.error("CloseableHttpAsyncClient closed failure!", e);
					}
				}
				if (syncClient != null) {
					try {
						syncClient.close();
					} catch (IOException e) {
						log.error("CloseableHttpClient closed failure!", e);
					}
				}
			}
		});
	}
}
