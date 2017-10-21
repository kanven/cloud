package com.kanven.cloud.client.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kanven.cloud.client.server.ClientService;

@RestController
@RequestMapping(value = "/client")
public class ClientController {

	@Autowired
	private ClientService clientService;

	@RequestMapping(value = "/test/{name}")
	public String brave(@PathVariable String name) {
		return clientService.hello(name);
	}

}
