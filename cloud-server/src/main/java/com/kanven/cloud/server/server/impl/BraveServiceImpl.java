package com.kanven.cloud.server.server.impl;

import com.kanven.server.server.api.BraveService;

public class BraveServiceImpl implements BraveService {

	@Override
	public String hello(String name) {
		return "hello," + name;
	}

}
