package com.gitenter.capsid.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.HashMap;
import java.util.Map;

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
		String password = "password";
		String userDisplayName = "Integration Test User";
		String userEmail = "user@it.user.com";
		Map<String, String> userRegistrationData = new HashMap<String, String>();
		userRegistrationData.put("username", username);
		userRegistrationData.put("password", password);
		userRegistrationData.put("displayName", userDisplayName);
		userRegistrationData.put("email", userEmail);
		Map<?, ?> registerUser = registerUser(userRegistrationData);
		assertEquals(registerUser.get("username"), username);

		String bearerToken = getBearerToken(username, password);
		
		assertEquals(getMe(bearerToken).get("username"), username);
		assertEquals(getUser((Integer)registerUser.get("id"), bearerToken).get("username"), username);
		
		userDisplayName = "Updated Integration Test User";
		userEmail = "updated@it.user.com";
		Map<String, String> userProfileData = new HashMap<String, String>();
		userProfileData.put("username", username);
		userProfileData.put("displayName", userDisplayName);
		userProfileData.put("email", userEmail);
		assertEquals(updateUser(userProfileData, bearerToken).get("displayName"), userDisplayName);
		
		final String oldPassword = password;
		password = "new_password";
		Map<String, String> changePasswordData = new HashMap<String, String>();
		changePasswordData.put("oldPassword", oldPassword);
		changePasswordData.put("newPassword", password);
		changePassword(changePasswordData, bearerToken);
		bearerToken = getBearerToken(username, password);
		
		final String sshKeyValue = "ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAABAQCvYWPKDryb70LRP1tePi9h1q2vebxFIQZn3MlPbp4XYKP+t+t325BlMbj6Tnvx55nDR5Q6CwPOBz5ijdv8yUEuQ9aaR3+CNvOqjrs7iE2mO4HPiE+w9tppNhOF37a/ElVuoKQtTrP4hFyQbdISVCpvhXx9MZZcaq+A8aLbcrL1ggydXiLpof6gyb9UgduXx90ntbahI5JZgNTZfZSzzCRu7of/zZYKr4dQLiCFGrGDnSs+j7Fq0GAGKywRz27UMh9ChE+PVy8AEOV5/Mycula2KWRhKU/DWZF5zaeVE4BliQjKtCJwhJGRz52OdFc55ic7JoDcF9ovEidnhw+VNnN9 user@email.com";
		Map<String, String> sshKeyData = new HashMap<String, String>();
		sshKeyData.put("sshKeyValue", sshKeyValue);
		assertEquals(addSshKey(sshKeyData, bearerToken).get("publicKey"), sshKeyValue);
		
		Map<?, ?>[] sshKeys = getSshKeys(bearerToken);
		assertEquals(sshKeys.length, 1);
		assertEquals(sshKeys[0].get("publicKey"), sshKeyValue);
		
		/*
		 * Test organization management
		 */
		final String organizationName = "it_org";
		String organizationDisplayName = "Integration Test Organization";
		Map<String, String> organizationData = new HashMap<String, String>();;
		organizationData.put("name", organizationName);
		organizationData.put("displayName", organizationDisplayName);
		Map<?, ?> createdOrganization = createOrganization(organizationData, bearerToken);
		assertEquals(createdOrganization.get("name"), organizationName);
		Integer createdOrganizationId = (Integer)createdOrganization.get("id");
		
		assertEquals(getOrganization(createdOrganizationId, bearerToken).get("name"), organizationName);
		
		Map<?, ?>[] managedOrganizations = getManagedOrganizations(bearerToken);
		assertEquals(managedOrganizations.length, 1);
		assertEquals(managedOrganizations[0].get("name"), organizationName);
		
		organizationDisplayName = "Updated Integration Test Organization";
		organizationData.put("displayName", organizationDisplayName);
		assertEquals(updateOrganization(createdOrganizationId, organizationData, bearerToken).get("displayName"), organizationDisplayName);
		
		Map<?, ?>[] organizationMembers = getOrganizationMembers(createdOrganizationId, bearerToken);
		assertEquals(organizationMembers.length, 1);
		assertEquals(organizationMembers[0].get("username"), username);
		assertTrue(organizationMembers[0].containsKey("mapId"));
		
		final String anotherUsername = "another_it_user";
		final String anotherPassword = "password";
		Map<String, String> anotherUserRegistrationData = new HashMap<String, String>();
		anotherUserRegistrationData.put("username", anotherUsername);
		anotherUserRegistrationData.put("password", anotherPassword);
		anotherUserRegistrationData.put("displayName", "Another Integration Test User");
		anotherUserRegistrationData.put("email", "anotheruser@it.user.com");
		registerUser(anotherUserRegistrationData);
		
		assertEquals(getOrganizationOrdinaryMembers(createdOrganizationId, bearerToken).length, 0);	
		Map<?, ?> anotherUserMap = addOrganizationOrdinaryMember(createdOrganizationId, anotherUsername, bearerToken);
		assertTrue(anotherUserMap.containsKey("mapId"));
		Integer anotherUserMapId = (Integer)(anotherUserMap.get("mapId"));
		assertEquals(getOrganizationOrdinaryMembers(createdOrganizationId, bearerToken).length, 1);
		assertEquals(getOrganizationManagers(createdOrganizationId, bearerToken).length, 1);
		promoteOrganizationManager(createdOrganizationId, anotherUserMapId, bearerToken);
		assertEquals(getOrganizationManagers(createdOrganizationId, bearerToken).length, 2);
		demoteOrganizationManager(createdOrganizationId, anotherUserMapId, bearerToken); 
		assertEquals(getOrganizationManagers(createdOrganizationId, bearerToken).length, 1);
		removeOrganizationMember(createdOrganizationId, anotherUserMapId, bearerToken);
		assertEquals(getOrganizationOrdinaryMembers(createdOrganizationId, bearerToken).length, 0);
		
		String anotherBearerToken = getBearerToken(anotherUsername, anotherPassword);
		deleteUser(anotherPassword, anotherBearerToken);
		
		deleteOrganization(createdOrganizationId, organizationName, bearerToken);
		
		/*
		 * TODO:
		 * Cleanups are better in a finally block, otherwise previous user journey failures
		 * will make the database in wrong state/make this test no longer idempotent. Better
		 * have a user journey framework to support it.
		 */
		deleteUser(password, bearerToken);
	}
	
	private Map<?, ?> registerUser(Map<?, ?> userRegistrationData) throws Exception {
		return objectMapper.readValue(
				mockMvc.perform(post("/api/users")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectWriter.writeValueAsString(userRegistrationData)))
						.andExpect(status().isOk())
						.andReturn().getResponse().getContentAsString(),
				Map.class);
	}
	
	private String getBearerToken(String username, String password) throws Exception {
		return new JSONObject(
				mockMvc.perform(post("/oauth/token")
						.contentType(MediaType.APPLICATION_FORM_URLENCODED)
						.with(httpBasic("gitenter-envuelope","secretpassword"))
						.content("username="+username+"&password="+password+"&grant_type=password"))
						.andExpect(status().isOk())
						.andReturn().getResponse().getContentAsString())
				.getString("access_token");
	}
	
	private Map<?, ?> getMe(String bearerToken) throws Exception {
		return objectMapper.readValue(
				mockMvc.perform(get("/api/users/me")
						.header("Authorization", "Bearer " + bearerToken))
						.andExpect(status().isOk())
						.andReturn().getResponse().getContentAsString(),
				Map.class);
	}
	
	private Map<?, ?> getUser(Integer userId, String bearerToken) throws Exception {
		return objectMapper.readValue(
				mockMvc.perform(get("/api/users/"+userId)
						.header("Authorization", "Bearer " + bearerToken))
						.andExpect(status().isOk())
						.andReturn().getResponse().getContentAsString(), 
				Map.class);
	}
	
	private Map<?, ?> updateUser(Map<?, ?> userProfileData, String bearerToken) throws Exception {
		return objectMapper.readValue(
				mockMvc.perform(put("/api/users/me")
						.header("Authorization", "Bearer " + bearerToken)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectWriter.writeValueAsString(userProfileData)))
						.andExpect(status().isOk())
						.andReturn().getResponse().getContentAsString(), 
				Map.class);
	}
	
	private void deleteUser(String password, String bearerToken) throws Exception {
		mockMvc.perform(delete("/api/users/me")
				.param("password", password)
				.header("Authorization", "Bearer " + bearerToken))
				.andExpect(status().isOk());
	}
	
	private void changePassword(Map<?, ?> changePasswordData, String bearerToken) throws Exception {
		mockMvc.perform(post("/api/users/me/password")
				.header("Authorization", "Bearer " + bearerToken)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectWriter.writeValueAsString(changePasswordData)))
				.andExpect(status().isOk());
	}
	
	private Map<?, ?> addSshKey(Map<?, ?> sshKeyData, String bearerToken) throws Exception {
		return objectMapper.readValue(
				mockMvc.perform(post("/api/users/me/ssh-keys")
						.header("Authorization", "Bearer " + bearerToken)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectWriter.writeValueAsString(sshKeyData)))
						.andExpect(status().isOk())
						.andReturn().getResponse().getContentAsString(),
				Map.class);
	}
	
	private Map<?, ?>[] getSshKeys(String bearerToken) throws Exception {
		return objectMapper.readValue(
				mockMvc.perform(get("/api/users/me/ssh-keys")
						.header("Authorization", "Bearer " + bearerToken))
						.andExpect(status().isOk())
						.andReturn().getResponse().getContentAsString(),
				Map[].class);
	}
	
	private Map<?, ?> createOrganization(Map<?, ?> organizationData, String bearerToken) throws Exception {
		return objectMapper.readValue(
				mockMvc.perform(post("/api/organizations")
						.header("Authorization", "Bearer " + bearerToken)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectWriter.writeValueAsString(organizationData)))
						.andExpect(status().isOk())
						.andReturn().getResponse().getContentAsString(), 
				Map.class);
	}
	
	private Map<?, ?> getOrganization(Integer organizationId, String bearerToken) throws Exception {
		return objectMapper.readValue(
				mockMvc.perform(get("/api/organizations/"+organizationId)
						.header("Authorization", "Bearer " + bearerToken))
						.andExpect(status().isOk())
						.andReturn().getResponse().getContentAsString(),
				Map.class);
	}
	
	private Map<?, ?>[] getManagedOrganizations(String bearerToken) throws Exception {
		return objectMapper.readValue(
				mockMvc.perform(get("/api/users/me/organizations?role=manager")
						.header("Authorization", "Bearer " + bearerToken))
						.andExpect(status().isOk())
						.andReturn().getResponse().getContentAsString(),
				Map[].class);
	}

	private Map<?, ?> updateOrganization(Integer organizationId, Map<?, ?> organizationData, String bearerToken) throws Exception {
		return objectMapper.readValue(
			mockMvc.perform(put("/api/organizations/"+organizationId)
					.header("Authorization", "Bearer " + bearerToken)
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectWriter.writeValueAsString(organizationData)))
					.andExpect(status().isOk())
					.andReturn().getResponse().getContentAsString(), 
			Map.class);
	}
	
	private void deleteOrganization(Integer organizationId, String organizationName, String bearerToken) throws Exception {
		mockMvc.perform(delete("/api/organizations/"+organizationId)
				.param("organization_name", organizationName)
				.header("Authorization", "Bearer " + bearerToken))
				.andExpect(status().isOk());
	}
	
	private Map<?, ?>[] getOrganizationMembers(Integer organizationId, String bearerToken) throws Exception {
		return objectMapper.readValue(
				mockMvc.perform(get("/api/organizations/"+organizationId+"/members")
						.header("Authorization", "Bearer " + bearerToken))
						.andExpect(status().isOk())
						.andReturn().getResponse().getContentAsString(),
				Map[].class);
	}
	
	private void removeOrganizationMember(Integer organizationId, Integer organizationUserMapId, String bearerToken) throws Exception {
		mockMvc.perform(delete("/api/organizations/"+organizationId+"/members/"+organizationUserMapId)
				.header("Authorization", "Bearer " + bearerToken))
				.andExpect(status().isOk());
	}
	
	private Map<?, ?>[] getOrganizationOrdinaryMembers(Integer organizationId, String bearerToken) throws Exception {
		return objectMapper.readValue(
				mockMvc.perform(get("/api/organizations/"+organizationId+"/ordinary-members")
						.header("Authorization", "Bearer " + bearerToken))
						.andExpect(status().isOk())
						.andReturn().getResponse().getContentAsString(),
				Map[].class);
	}
	
	private Map<?, ?> addOrganizationOrdinaryMember(Integer organizationId, String username, String bearerToken) throws Exception {
		return objectMapper.readValue(
				mockMvc.perform(post("/api/organizations/"+organizationId+"/ordinary-members")
						.param("username", username)
						.header("Authorization", "Bearer " + bearerToken))
						.andExpect(status().isOk())
						.andReturn().getResponse().getContentAsString(),
				Map.class);
	}
	
	private Map<?, ?>[] getOrganizationManagers(Integer organizationId, String bearerToken) throws Exception {
		return objectMapper.readValue(
				mockMvc.perform(get("/api/organizations/"+organizationId+"/managers")
						.header("Authorization", "Bearer " + bearerToken))
						.andExpect(status().isOk())
						.andReturn().getResponse().getContentAsString(),
				Map[].class);
	}
	
	private void promoteOrganizationManager(Integer organizationId, Integer organizationUserMapId, String bearerToken) throws Exception {
		mockMvc.perform(post("/api/organizations/"+organizationId+"/managers/"+organizationUserMapId)
				.header("Authorization", "Bearer " + bearerToken))
				.andExpect(status().isOk());
	}
	
	private void demoteOrganizationManager(Integer organizationId, Integer organizationUserMapId, String bearerToken) throws Exception {
		mockMvc.perform(delete("/api/organizations/"+organizationId+"/managers/"+organizationUserMapId)
				.header("Authorization", "Bearer " + bearerToken))
				.andExpect(status().isOk());
	}
}
