package com.gitenter.capsid.web;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/*
 * Need profile because this test will actually talk to the database,
 * and the local one exist.
 * Test passes fine locally without `@ActiveProfiles("local")`, but in
 * CI pipeline it will fail by complaining 
 * > Caused by: java.net.UnknownHostException: staging-ecs.cqx7dy9nh94t.us-east-1.rds.amazonaws.com
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("local")
public class HealthCheckControllerHttpRequestTest {
	
	@LocalServerPort private int port;

	@Autowired private TestRestTemplate restTemplate;

	@Test
	public void healthCheckEndpointShouldReturnDefaultMessage() throws Exception {
		assertThat(this.restTemplate.getForObject(
				"http://localhost:" + port + "/health_check", 
				String.class)).contains("GitEnter");
	}
}
