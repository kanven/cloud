package com.kanven.cloud.client.server.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.kanven.cloud.client.server.ClientService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath*:spring/applicationContext-message.xml" })
public class ClientServiceImplTest {

	@Autowired
	private ClientService clientService;

	@Test
	public void testHello() {
		System.out.println(clientService.hello("jiangyl"));
	}

}
