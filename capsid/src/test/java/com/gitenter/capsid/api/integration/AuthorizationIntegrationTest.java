package com.gitenter.capsid.api.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.gitenter.capsid.dto.UserRegisterDTO;
import com.gitenter.protease.domain.auth.UserBean;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("local")
public class AuthorizationIntegrationTest {
	
	@Autowired MockMvc mockMvc;
	
	private ObjectMapper objectMapper;
	
	private final String username = "integration_test";
	private final String password = "password";
	
	@BeforeEach
	public void setUp() throws JsonProcessingException {
		objectMapper = new ObjectMapper();
		objectMapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
	}
	
	@Test
	public void testOAuth2Authorization() throws Exception {
		
		UserRegisterDTO userRegisterDTO = new UserRegisterDTO();
		userRegisterDTO.setUsername(username);
		userRegisterDTO.setPassword(password);
		userRegisterDTO.setDisplayName("Integration Test");
		userRegisterDTO.setEmail("integration@test.user.com");
		
		ObjectWriter ow = objectMapper.writer().withDefaultPrettyPrinter();
		String userRegisterDTOJson = ow.writeValueAsString(userRegisterDTO);
		
		String registerUserJson = mockMvc.perform(post("/api/register")
				.contentType(MediaType.APPLICATION_JSON)
				.content(userRegisterDTOJson))
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();
		UserBean registerUser = objectMapper.readValue(registerUserJson, UserBean.class);
		assertEquals(registerUser.getUsername(), username);
		
		String tokenJson = mockMvc.perform(post("/oauth/token")
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.with(httpBasic("gitenter-envuelope","secretpassword"))
				.content("username="+username+"&password="+password+"&grant_type=password"))
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();
		String bearerToken = new JSONObject(tokenJson).getString("access_token");
		
		String queryUserJson = mockMvc.perform(get("/api/users/"+registerUser.getId())
				.header("Authorization", "Bearer " + bearerToken))
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();
		UserBean queryUser = objectMapper.readValue(queryUserJson, UserBean.class);
		assertEquals(queryUser.getUsername(), username);
	}
}
