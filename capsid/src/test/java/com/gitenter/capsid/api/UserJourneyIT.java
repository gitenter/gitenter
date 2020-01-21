package com.gitenter.capsid.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
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
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.gitenter.capsid.dto.OrganizationDTO;
import com.gitenter.capsid.dto.UserRegisterDTO;
import com.gitenter.protease.domain.auth.OrganizationBean;
import com.gitenter.protease.domain.auth.UserBean;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("local")
public class UserJourneyIT {
	
	@Autowired MockMvc mockMvc;
	
	private ObjectMapper objectMapper;
	private ObjectWriter objectWriter;
	
	@BeforeEach
	public void setUp() throws JsonProcessingException {
		objectMapper = new ObjectMapper();
		
		/*
		 * `DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES` is for HATEOAS `_link`.
		 */
		objectMapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false); 
		
		objectWriter = objectMapper.writer().withDefaultPrettyPrinter();
	}
	
	@Test
	public void testUserJourney() throws Exception {
		
		/*
		 * Test user registration and OAuth
		 */
		final String username = "it_user";
		final String password = "password";
		
		UserRegisterDTO userRegisterDTO = new UserRegisterDTO();
		userRegisterDTO.setUsername(username);
		userRegisterDTO.setPassword(password);
		userRegisterDTO.setDisplayName("Integration Test User");
		userRegisterDTO.setEmail("integration@test.user.com");
		
		String userRegisterDTOJson = objectWriter.writeValueAsString(userRegisterDTO);
		
		UserBean registerUser = objectMapper.readValue(
				mockMvc.perform(post("/api/users")
						.contentType(MediaType.APPLICATION_JSON)
						.content(userRegisterDTOJson))
						.andExpect(status().isOk())
						.andReturn().getResponse().getContentAsString(), 
				UserBean.class);
		assertEquals(registerUser.getUsername(), username);

		String bearerToken = new JSONObject(
				mockMvc.perform(post("/oauth/token")
						.contentType(MediaType.APPLICATION_FORM_URLENCODED)
						.with(httpBasic("gitenter-envuelope","secretpassword"))
						.content("username="+username+"&password="+password+"&grant_type=password"))
						.andExpect(status().isOk())
						.andReturn().getResponse().getContentAsString())
				.getString("access_token");
		
		UserBean queryMe = objectMapper.readValue(
				mockMvc.perform(get("/api/users/me")
						.header("Authorization", "Bearer " + bearerToken))
						.andExpect(status().isOk())
						.andReturn().getResponse().getContentAsString(),
				UserBean.class);
		assertEquals(queryMe.getUsername(), username);
		
		UserBean queryUser = objectMapper.readValue(
				mockMvc.perform(get("/api/users/"+registerUser.getId())
						.header("Authorization", "Bearer " + bearerToken))
						.andExpect(status().isOk())
						.andReturn().getResponse().getContentAsString(), 
				UserBean.class);
		assertEquals(queryUser.getUsername(), username);
		
		/*
		 * Test organization management
		 */
		final String organizationName = "it_org";
		
		OrganizationDTO organizationDTO = new OrganizationDTO();
		organizationDTO.setName(organizationName);
		organizationDTO.setDisplayName("Integration Test Organization");
		
		String organizationDTOJson = objectWriter.writeValueAsString(organizationDTO);
		
		OrganizationBean createdOrganization = objectMapper.readValue(
				mockMvc.perform(post("/api/organizations")
						.header("Authorization", "Bearer " + bearerToken)
						.contentType(MediaType.APPLICATION_JSON)
						.content(organizationDTOJson))
						.andExpect(status().isOk())
						.andReturn().getResponse().getContentAsString(), 
				OrganizationBean.class);
		assertEquals(createdOrganization.getName(), organizationName);
		
		OrganizationBean queryOrganization = objectMapper.readValue(
				mockMvc.perform(get("/api/organizations/"+createdOrganization.getId())
						.header("Authorization", "Bearer " + bearerToken))
						.andExpect(status().isOk())
						.andReturn().getResponse().getContentAsString(),
				OrganizationBean.class);
		assertEquals(queryOrganization.getName(), organizationName);
		
		mockMvc.perform(delete("/api/organizations/"+createdOrganization.getId())
				.param("organization_name", organizationName)
				.header("Authorization", "Bearer " + bearerToken))
				.andExpect(status().isOk());
		
		mockMvc.perform(delete("/api/users/me")
				.param("password", password)
				.header("Authorization", "Bearer " + bearerToken))
				.andExpect(status().isOk());
	}
}
