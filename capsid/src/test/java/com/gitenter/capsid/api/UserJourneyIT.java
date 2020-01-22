package com.gitenter.capsid.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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
import com.gitenter.capsid.dto.ChangePasswordDTO;
import com.gitenter.capsid.dto.OrganizationDTO;
import com.gitenter.capsid.dto.SshKeyFieldDTO;
import com.gitenter.capsid.dto.UserProfileDTO;
import com.gitenter.capsid.dto.UserRegisterDTO;
import com.gitenter.protease.domain.auth.OrganizationBean;
import com.gitenter.protease.domain.auth.SshKeyBean;
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
		String password = "password";
		String userDisplayName = "Integration Test User";
		String userEmail = "user@it.user.com";
		
		UserRegisterDTO userRegisterDTO = new UserRegisterDTO();
		userRegisterDTO.setUsername(username);
		userRegisterDTO.setPassword(password);
		userRegisterDTO.setDisplayName(userDisplayName);
		userRegisterDTO.setEmail(userEmail);
		
		UserBean registerUser = registerUser(userRegisterDTO);
		assertEquals(registerUser.getUsername(), username);

		String bearerToken = getBearerToken(username, password);
		
		assertEquals(getMe(bearerToken).getUsername(), username);
		assertEquals(getUser(registerUser.getId(), bearerToken).getUsername(), username);
		
		userDisplayName = "Updated Integration Test User";
		userEmail = "updated@it.user.com";
		
		UserProfileDTO userProfileDTO = new UserProfileDTO();
		userProfileDTO.setUsername(username);
		userProfileDTO.setDisplayName(userDisplayName);
		userProfileDTO.setEmail(userEmail);
		
		assertEquals(updateUser(userProfileDTO, bearerToken).getDisplayName(), userDisplayName);
		
		final String oldPassword = password;
		password = "new_password";
		
		ChangePasswordDTO changePasswordDTO = new ChangePasswordDTO();
		changePasswordDTO.setOldPassword(oldPassword);
		changePasswordDTO.setNewPassword(password);

		changePassword(changePasswordDTO, bearerToken);
		bearerToken = getBearerToken(username, password);
		
		final String sshKeyValue = "ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAABAQCvYWPKDryb70LRP1tePi9h1q2vebxFIQZn3MlPbp4XYKP+t+t325BlMbj6Tnvx55nDR5Q6CwPOBz5ijdv8yUEuQ9aaR3+CNvOqjrs7iE2mO4HPiE+w9tppNhOF37a/ElVuoKQtTrP4hFyQbdISVCpvhXx9MZZcaq+A8aLbcrL1ggydXiLpof6gyb9UgduXx90ntbahI5JZgNTZfZSzzCRu7of/zZYKr4dQLiCFGrGDnSs+j7Fq0GAGKywRz27UMh9ChE+PVy8AEOV5/Mycula2KWRhKU/DWZF5zaeVE4BliQjKtCJwhJGRz52OdFc55ic7JoDcF9ovEidnhw+VNnN9 user@email.com";
		
		SshKeyFieldDTO sshKeyFieldDTO = new SshKeyFieldDTO();
		sshKeyFieldDTO.setSshKeyValue(sshKeyValue);
		
		assertEquals(addSshKey(sshKeyFieldDTO, bearerToken).getPublicKey(), sshKeyValue);
		
		SshKeyBean[] sshKeys = getSshKeys(bearerToken);
		assertEquals(sshKeys.length, 1);
		assertEquals(sshKeys[0].getPublicKey(), sshKeyValue);
		
		/*
		 * Test organization management
		 */
		final String organizationName = "it_org";
		String organizationDisplayName = "Integration Test Organization";
		
		OrganizationDTO organizationDTO = new OrganizationDTO();
		organizationDTO.setName(organizationName);
		organizationDTO.setDisplayName(organizationDisplayName);

		OrganizationBean createdOrganization = createOrganization(organizationDTO, bearerToken);
		assertEquals(createdOrganization.getName(), organizationName);
		
		assertEquals(getOrganization(createdOrganization.getId(), bearerToken).getName(), organizationName);
		
		OrganizationBean[] managedOrganizations = getManagedOrganizations(bearerToken);
		assertEquals(managedOrganizations.length, 1);
		assertEquals(managedOrganizations[0].getName(), organizationName);
		
		organizationDisplayName = "Updated Integration Test Organization";
		
		organizationDTO.setDisplayName(organizationDisplayName);
		
		assertEquals(updateOrganization(createdOrganization.getId(), organizationDTO, bearerToken).getDisplayName(), organizationDisplayName);
		
		UserBean[] organizationMembers = getOrganizationMembers(createdOrganization, bearerToken);
		assertEquals(organizationMembers.length, 1);
		assertEquals(organizationMembers[0].getUsername(), username);
		
		deleteOrganization(createdOrganization, organizationName, bearerToken);
		
		/*
		 * TODO:
		 * Cleanups are better in a finally block, otherwise previous user journey failures
		 * will make the database in wrong state/make this test no longer idempotent. Better
		 * have a user journey framework to support it.
		 */
		deleteUser(password, bearerToken);
	}
	
	private UserBean registerUser(UserRegisterDTO userRegisterDTO) throws Exception {
		return objectMapper.readValue(
				mockMvc.perform(post("/api/users")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectWriter.writeValueAsString(userRegisterDTO)))
						.andExpect(status().isOk())
						.andReturn().getResponse().getContentAsString(),
				UserBean.class);
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
	
	private UserBean getMe(String bearerToken) throws Exception {
		return objectMapper.readValue(
				mockMvc.perform(get("/api/users/me")
						.header("Authorization", "Bearer " + bearerToken))
						.andExpect(status().isOk())
						.andReturn().getResponse().getContentAsString(),
				UserBean.class);
	}
	
	private UserBean getUser(Integer userId, String bearerToken) throws Exception {
		return objectMapper.readValue(
				mockMvc.perform(get("/api/users/"+userId)
						.header("Authorization", "Bearer " + bearerToken))
						.andExpect(status().isOk())
						.andReturn().getResponse().getContentAsString(), 
				UserBean.class);
	}
	
	private UserBean updateUser(UserProfileDTO userProfileDTO, String bearerToken) throws Exception {
		return objectMapper.readValue(
				mockMvc.perform(put("/api/users/me")
						.header("Authorization", "Bearer " + bearerToken)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectWriter.writeValueAsString(userProfileDTO)))
						.andExpect(status().isOk())
						.andReturn().getResponse().getContentAsString(), 
				UserBean.class);
	}
	
	private void changePassword(ChangePasswordDTO changePasswordDTO, String bearerToken) throws Exception {
		mockMvc.perform(post("/api/users/me/password")
				.header("Authorization", "Bearer " + bearerToken)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectWriter.writeValueAsString(changePasswordDTO)))
				.andExpect(status().isOk());
	}
	
	private SshKeyBean addSshKey(SshKeyFieldDTO sshKeyFieldDTO, String bearerToken) throws Exception {
		return objectMapper.readValue(
				mockMvc.perform(post("/api/users/me/ssh-keys")
						.header("Authorization", "Bearer " + bearerToken)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectWriter.writeValueAsString(sshKeyFieldDTO)))
						.andExpect(status().isOk())
						.andReturn().getResponse().getContentAsString(),
				SshKeyBean.class);
	}
	
	private SshKeyBean[] getSshKeys(String bearerToken) throws Exception {
		return objectMapper.readValue(
				mockMvc.perform(get("/api/users/me/ssh-keys")
						.header("Authorization", "Bearer " + bearerToken))
						.andExpect(status().isOk())
						.andReturn().getResponse().getContentAsString(),
				SshKeyBean[].class);
	}
	
	private OrganizationBean createOrganization(OrganizationDTO organizationDTO, String bearerToken) throws Exception {
		return objectMapper.readValue(
				mockMvc.perform(post("/api/organizations")
						.header("Authorization", "Bearer " + bearerToken)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectWriter.writeValueAsString(organizationDTO)))
						.andExpect(status().isOk())
						.andReturn().getResponse().getContentAsString(), 
				OrganizationBean.class);
	}
	
	private OrganizationBean getOrganization(Integer organizationId, String bearerToken) throws Exception {
		return objectMapper.readValue(
				mockMvc.perform(get("/api/organizations/"+organizationId)
						.header("Authorization", "Bearer " + bearerToken))
						.andExpect(status().isOk())
						.andReturn().getResponse().getContentAsString(),
				OrganizationBean.class);
	}
	
	private OrganizationBean[] getManagedOrganizations(String bearerToken) throws Exception {
		return objectMapper.readValue(
				mockMvc.perform(get("/api/users/me/organizations?role=manager")
						.header("Authorization", "Bearer " + bearerToken))
						.andExpect(status().isOk())
						.andReturn().getResponse().getContentAsString(),
				OrganizationBean[].class);
	}

	private OrganizationBean updateOrganization(Integer organizationId, OrganizationDTO organizationDTO, String bearerToken) throws Exception {
		return objectMapper.readValue(
			mockMvc.perform(put("/api/organizations/"+organizationId)
					.header("Authorization", "Bearer " + bearerToken)
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectWriter.writeValueAsString(organizationDTO)))
					.andExpect(status().isOk())
					.andReturn().getResponse().getContentAsString(), 
			OrganizationBean.class);
	}
	
	private UserBean[] getOrganizationMembers(OrganizationBean createdOrganization, String bearerToken) throws Exception {
		return objectMapper.readValue(
				mockMvc.perform(get("/api/organizations/"+createdOrganization.getId()+"/members")
						.header("Authorization", "Bearer " + bearerToken))
						.andExpect(status().isOk())
						.andReturn().getResponse().getContentAsString(),
				UserBean[].class);
	}
	
	private void deleteOrganization(OrganizationBean createdOrganization, String organizationName, String bearerToken) throws Exception {
		mockMvc.perform(delete("/api/organizations/"+createdOrganization.getId())
				.param("organization_name", organizationName)
				.header("Authorization", "Bearer " + bearerToken))
				.andExpect(status().isOk());
	}
	
	private void deleteUser(String password, String bearerToken) throws Exception {
		mockMvc.perform(delete("/api/users/me")
				.param("password", password)
				.header("Authorization", "Bearer " + bearerToken))
				.andExpect(status().isOk());
	}
}
