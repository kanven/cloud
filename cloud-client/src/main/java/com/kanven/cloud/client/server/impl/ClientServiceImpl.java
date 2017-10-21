package com.kanven.cloud.client.server.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.kanven.cloud.client.server.ClientService;
import com.kanven.server.server.api.BraveService;

@Component
public class ClientServiceImpl implements ClientService {

	@Autowired
	private BraveService braveService;

	@Override
	public String hello(String name) {
		return braveService.hello(name);
	}

}
