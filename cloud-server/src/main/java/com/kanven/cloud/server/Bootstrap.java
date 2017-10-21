package com.kanven.cloud.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.weibo.api.motan.common.MotanConstants;
import com.weibo.api.motan.util.MotanSwitcherUtil;

@EnableWebMvc
@PropertySource(value = { "classpath:brave.properties", "classpath:config.properties" })
@ComponentScan(basePackages = { "com.kanven.cloud.server", "com.kanven.cloud.common" })
@ImportResource(locations = { "classpath*:spring/applicationContext-motan.xml" })
@SpringBootApplication
public class Bootstrap {

	public static void main(String[] args) {
		SpringApplication.run(Bootstrap.class, args);
		MotanSwitcherUtil.setSwitcherValue(MotanConstants.REGISTRY_HEARTBEAT_SWITCHER, true);
	}

}
