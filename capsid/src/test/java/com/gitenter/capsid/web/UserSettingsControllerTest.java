package com.gitenter.capsid.web;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.apache.commons.lang3.StringUtils;
import org.junit.Ignore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.gitenter.capsid.dto.UserRegisterDTO;
import com.gitenter.capsid.service.UserService;

public class UserSettingsControllerTest {
	
	private MockMvc mockMvc;
	
	private UserService mockUserService;
	
	@BeforeEach
	public void setUp() throws Exception {

		mockUserService = mock(UserService.class);
		UserSettingsController controller = new UserSettingsController(mockUserService);
		mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
	}
	
	@Test
	public void testUpdateProfileWithValidInput() throws Exception {
		
		mockMvc.perform(post("/settings/profile")
				.param("username", "username")
				.param("displayName", "User Name")
				.param("email", "username@email.com"))
		.andExpect(flash().attribute("successfulMessage", "Changes has been saved successfully!"))
		.andExpect(redirectedUrl("/settings/profile"));
	}
	
	@Test
	public void testUpdateProfileWithMissingInputs() throws Exception {
		
		mockMvc.perform(post("/settings/profile")
				.param("username", "username"))
		.andExpect(view().name("settings/profile"))
		.andExpect(status().isOk())
		.andExpect(model().errorCount(2))
		.andExpect(model().attributeHasFieldErrors(
				"memberProfileDTO", "displayName", "email"))
		.andReturn().getResponse().getContentAsString().contains("must not be null");
	}
	
	@Test
	public void testUpdateProfileWithInvalidEmail() throws Exception {
		
		mockMvc.perform(post("/settings/profile")
				.param("username", "username")
				.param("displayName", "User Name")
				.param("email", "not_a_valid_email"))
		.andExpect(view().name("settings/profile"))
		.andExpect(status().isOk())
		.andExpect(model().errorCount(1))
		.andExpect(model().attributeHasFieldErrors(
				"memberProfileDTO", "email"))
		.andReturn().getResponse().getContentAsString().contains("not a well-formed email address");
	}
	
	@Test
	public void testUpdateProfileWithInputTooShort() throws Exception {
		
		mockMvc.perform(post("/settings/profile")
				.param("username", "username")
				.param("displayName", "D")
				.param("email", "username@email.com"))
		.andExpect(view().name("settings/profile"))
		.andExpect(status().isOk())
		.andExpect(model().errorCount(1))
		.andExpect(model().attributeHasFieldErrors(
				"memberProfileDTO", "displayName"))
		.andReturn().getResponse().getContentAsString().contains("size must be between");
	}
	
	@Test
	public void testUpdateProfileWithInputTooLong() throws Exception {
		
		mockMvc.perform(post("/settings/profile")
				.param("username", "username")
				.param("displayName", StringUtils.repeat('x', 65))
				.param("email", "username@email.com"))
		.andExpect(view().name("settings/profile"))
		.andExpect(status().isOk())
		.andExpect(model().errorCount(1))
		.andExpect(model().attributeHasFieldErrors(
				"memberProfileDTO", "displayName"))
		.andReturn().getResponse().getContentAsString().contains("size must be between");
	}
	
	@Test
	public void testUpdatePassword() throws Exception {
		
		when(mockUserService.updatePassword(any(UserRegisterDTO.class), eq("correct_old_password")))
		.thenReturn(true);
		
		when(mockUserService.updatePassword(any(UserRegisterDTO.class), eq("wrong_old_password")))
		.thenReturn(false);
		
		mockMvc.perform(post("/settings/account/password")
				.param("username", "username")
				.param("password", "new_password")
				.param("old_password", "correct_old_password"))
		.andExpect(flash().attribute("successfulMessage", "Changes has been saved successfully!"))
		.andExpect(redirectedUrl("/settings/account/password"));
		
		mockMvc.perform(post("/settings/account/password")
				.param("username", "username")
				.param("password", "new_password")
				.param("old_password", "wrong_old_password"))
		.andExpect(flash().attribute("errorMessage", "Old password doesn't match!"))
		.andExpect(redirectedUrl("/settings/account/password"));
	}
	
	@Test
	public void testUpdatePasswordInputTooShort() throws Exception {
		
		mockMvc.perform(post("/settings/account/password")
				.param("username", "username")
				.param("password", "p")
				.param("old_password", "password"))
		.andExpect(view().name("settings/account/password"))
		.andExpect(status().isOk())
		.andExpect(model().errorCount(2+1))
		.andExpect(model().attributeHasFieldErrors(
				"memberRegisterDTO", "password"))
		.andReturn().getResponse().getContentAsString().contains("size must be between");
	}
	
	/*
	 * TODO:
	 * Cannot test `/settings/ssh` URL because `Authentication authentication`
	 * is not properly mocked. Tried `@WithMockUser(username = "username")`
	 * with `@RunWith(SpringRunner.class)` and `@SpringBootTest` but it seems
	 * doesn't work.
	 * 
	 * One possibility is to not input `Authentication authentication` in
	 * controller layer. Rather, we can `@PreAuthorize("isAuthenticated()")`
	 * in the service layer and get user information from there. Not sure if
	 * that's a better solution.
	 */
	@Ignore
	public void testAddSshKeyValidInput() throws Exception {
		
		mockMvc.perform(post("/settings/ssh")
				.param("value", "ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAABAQCvYWPKDryb70LRP1tePi9h1q2vebxFIQZn3MlPbp4XYKP+t+t325BlMbj6Tnvx55nDR5Q6CwPOBz5ijdv8yUEuQ9aaR3+CNvOqjrs7iE2mO4HPiE+w9tppNhOF37a/ElVuoKQtTrP4hFyQbdISVCpvhXx9MZZcaq+A8aLbcrL1ggydXiLpof6gyb9UgduXx90ntbahI5JZgNTZfZSzzCRu7of/zZYKr4dQLiCFGrGDnSs+j7Fq0GAGKywRz27UMh9ChE+PVy8AEOV5/Mycula2KWRhKU/DWZF5zaeVE4BliQjKtCJwhJGRz52OdFc55ic7JoDcF9ovEidnhw+VNnN9 user@email.com"))
		.andExpect(redirectedUrl("/settings/ssh"));
	}
	
	@Ignore
	public void testAddSshKeyInalidInput() throws Exception {
		
		mockMvc.perform(post("/settings/ssh")
				.param("value", "invalid_ssh_key"))
		.andExpect(view().name("settings/account/password"))
		.andExpect(status().isOk())
		.andExpect(model().errorCount(1))
		.andExpect(model().attributeHasFieldErrors(
				"sshKeyFieldDTO", "value"))
		.andReturn().getResponse().getContentAsString().contains("The SSH key does not have a valid format!");
	}
}
