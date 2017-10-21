package com.kanven.cloud.client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.weibo.api.motan.common.MotanConstants;
import com.weibo.api.motan.util.MotanSwitcherUtil;

@EnableWebMvc
@PropertySource(value = { "classpath:brave.properties", "classpath:config.properties" })
@ComponentScan(basePackages = { "com.kanven.cloud.client", "com.kanven.cloud.common" })
@ImportResource(locations = { "classpath*:spring/applicationContext-motan.xml" })
@SpringBootApplication
public class Bootstrap {

	@Bean
	RestTemplate template() {
		return new RestTemplate();
	}

	public static void main(String[] args) {
		SpringApplication.run(Bootstrap.class, args);
		MotanSwitcherUtil.setSwitcherValue(MotanConstants.REGISTRY_HEARTBEAT_SWITCHER, true);
	}

}
