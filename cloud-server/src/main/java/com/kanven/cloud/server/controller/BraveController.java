package com.kanven.cloud.server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping(value = "/brave")
public class BraveController {

	@Autowired
	private RestTemplate template;

	@RequestMapping(value = "/test/{name}")
	public String brave(@PathVariable String name) {
		return template.getForObject("http://localhost:8080/cloud/client/hello/{name}", String.class, name);
	}

}
