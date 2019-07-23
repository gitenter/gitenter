package com.gitenter.capsid.web;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

/*
 * Need profile because this test will actually talk to the database,
 * and the local one exist.
 * Test passes fine locally without `@ActiveProfiles("sts")`, but in
 * CI pipeline it will fail by complaining 
 * > Caused by: java.net.UnknownHostException: staging-postgres.cqx7dy9nh94t.us-east-1.rds.amazonaws.com
 */
@RunWith(SpringRunner.class)
@WebMvcTest(HealthCheckController.class)
@ActiveProfiles("sts")
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
