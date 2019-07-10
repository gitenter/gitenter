package com.gitenter.capsid.web;

import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.gitenter.capsid.service.MemberService;

public class MemberSettingsControllerTest {
	
	private static MockMvc mockMvc;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {

		MemberService mockService = mock(MemberService.class);
		MemberSettingsController controller = new MemberSettingsController(mockService);
		mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
	}
	
	@Test
	public void testUpdateProfileWithValidInput() throws Exception {
		
		mockMvc.perform(post("/settings/profile")
				.param("username", "username")
				.param("displayName", "User Name")
				.param("email", "username@email.com"))
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
		.andReturn().getResponse().getContentAsString().contains("size must be between 2 and 64");
	}
	
	@Test
	public void testUpdateProfileWithInputTooLong() throws Exception {
		
		mockMvc.perform(post("/settings/profile")
				.param("username", "username")
				.param("displayName", "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx")
				.param("email", "username@email.com"))
		.andExpect(view().name("settings/profile"))
		.andExpect(status().isOk())
		.andExpect(model().errorCount(1))
		.andExpect(model().attributeHasFieldErrors(
				"memberProfileDTO", "displayName"))
		.andReturn().getResponse().getContentAsString().contains("size must be between");
	}
}
