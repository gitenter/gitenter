package com.gitenter.capsid.web;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

/*
 * Need profile because this test will actually talk to the database,
 * and the local one exist.
 * Test passes fine locally without `@ActiveProfiles("local")`, but in
 * CI pipeline it will fail by complaining 
 * > Caused by: java.net.UnknownHostException: staging-ecs.cqx7dy9nh94t.us-east-1.rds.amazonaws.com
 */
@ExtendWith(SpringExtension.class)
@WebMvcTest(HealthCheckController.class)
@ActiveProfiles("local")
public class HealthCheckControllerMockMvcTest {
	
	@Autowired private MockMvc mockMvc;

	@Test
	public void healthCheckEndpointShouldReturnDefaultMessage() throws Exception {
		mockMvc.perform(get("/health_check"))
		.andDo(print())
		.andExpect(status().isOk())
		.andExpect(content().string(containsString("GitEnter")));
	}
}
