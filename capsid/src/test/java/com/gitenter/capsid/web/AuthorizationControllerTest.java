package com.gitenter.capsid.web;

import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.gitenter.capsid.service.AnonymousService;

public class AuthorizationControllerTest {
	
	@Autowired
	private MockMvc mockMvc;
	
	@MockBean
    private AnonymousService mockService;
	
	@BeforeEach
	public void setUp() throws Exception {

		/*
		 * TODO:
		 * For some weird reason it doesn't work by using `@WebMvcTest`
		 * with autowired `MockMvc` and `@MockBean`. For that case 
		 * we'll get 403 for POST with input.
		 */
		AnonymousService mockService = mock(AnonymousService.class);
		AuthorizationController controller = new AuthorizationController(mockService);
		mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
	}

	@Test
	public void testShowRegisterPage() throws Exception {
		
		mockMvc.perform(get("/register"))
		.andExpect(view().name("authorization/register"))
		.andExpect(model().attributeExists("userRegisterDTO"));
	}
	
	@Test
	public void testShowLoginPage() throws Exception {
		
		mockMvc.perform(get("/login"))
		.andExpect(view().name("authorization/login"))
		.andExpect(model().attributeExists("loginDTO"));
		
		mockMvc.perform(get("/login").param("error", "xxxx"))
		.andExpect(view().name("authorization/login"))
		.andExpect(model().attributeExists("message"))
		.andExpect(model().attributeExists("loginDTO"));
	}
	
	@Test
	public void testProcessRegistrationWithValidInput() throws Exception {
		
		mockMvc.perform(post("/register")
				.param("username", "username")
				.param("password", "password")
				.param("displayName", "User Name")
				.param("email", "username@email.com"))
		.andExpect(redirectedUrl("/login"));
	}
	
	@Test
	public void testProcessRegistrationWithMissingInputs() throws Exception {
		
		mockMvc.perform(post("/register")) 
		.andExpect(view().name("authorization/register"))
		.andExpect(status().isOk())
		.andExpect(model().errorCount(4))
		.andExpect(model().attributeHasFieldErrors(
				"userRegisterDTO", "username", "password", "displayName", "email"))
		.andReturn().getResponse().getContentAsString().contains("must not be null");
		
		/*
		 * TODO:
		 * Cannot completely replace the anomaly E2E tests, as it is not tested in here
		 * on the UI that the error messages can be rendered. If we want to include 
		 * view layer test we can probably go with:
		 * https://docs.spring.io/spring-test-htmlunit/docs/current/reference/html5/
		 */
	}
	
	@Test
	public void testProcessRegistrationWithInvalidEmail() throws Exception {
		
		mockMvc.perform(post("/register")
				.param("username", "username")
				.param("password", "password")
				.param("displayName", "User Name")
				.param("email", "not_a_valid_email"))
		.andExpect(view().name("authorization/register"))
		.andExpect(status().isOk())
		.andExpect(model().errorCount(1))
		.andExpect(model().attributeHasFieldErrors(
				"userRegisterDTO", "email"))
		.andReturn().getResponse().getContentAsString().contains("not a well-formed email address");
	}
	
	@Test
	public void testProcessRegistrationWithInputTooShort() throws Exception {
		
		mockMvc.perform(post("/register")
				.param("username", "u")
				.param("password", "p")
				.param("displayName", "D")
				.param("email", "username@email.com"))
		.andExpect(view().name("authorization/register"))
		.andExpect(status().isOk())
		.andExpect(model().errorCount(3))
		.andExpect(model().attributeHasFieldErrors(
				"userRegisterDTO", "username", "password", "displayName"))
		.andReturn().getResponse().getContentAsString().contains("size must be between 2 and 64");
	}
	
	@Test
	public void testProcessRegistrationWithInputTooLong() throws Exception {
		
		mockMvc.perform(post("/register")
				.param("username", StringUtils.repeat('x', 17))
				.param("password", StringUtils.repeat('x', 17))
				.param("displayName", StringUtils.repeat('x', 65))
				.param("email", "username@email.com"))
		.andExpect(view().name("authorization/register"))
		.andExpect(status().isOk())
		.andExpect(model().errorCount(3))
		.andExpect(model().attributeHasFieldErrors(
				"userRegisterDTO", "username", "password", "displayName"))
		.andReturn().getResponse().getContentAsString().contains("size must be between");
	}
}
