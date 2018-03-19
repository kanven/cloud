package com.kanven.cloud.common.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.autoconfigure.web.HttpMessageConverters;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.StringHttpMessageConverter;

import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;

@Configuration
public class FastJsonMessageConverterConfig {

	@Bean
	public HttpMessageConverters fastJsonHttpMessageConverters() {
		StringHttpMessageConverter converter = new StringHttpMessageConverter();
		List<MediaType> types = new ArrayList<MediaType>();
		types.add(MediaType.APPLICATION_JSON_UTF8);
		converter.setSupportedMediaTypes(types);
		return new HttpMessageConverters(converter, new FastJsonHttpMessageConverter());
	}

}
