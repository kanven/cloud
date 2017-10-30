package com.kanven.cloud.client.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.kanven.cloud.client.server.ClientService;

@RestController
@RequestMapping(value = "/client")
public class ClientController {

	@Autowired
	private ClientService clientService;

	@Autowired
	private RestTemplate template;

	@RequestMapping(value = "/test/{name}")
	public String brave(@PathVariable String name) {
		return clientService.hello(name);
	}

	@RequestMapping(value = "/http/{name}")
	public String http(@PathVariable String name) {
		ResponseEntity<String> response = template.getForEntity("http://localhost:8090/cloud/brave/hello/" + name,
				String.class);
		return response.getBody();
	}

}
