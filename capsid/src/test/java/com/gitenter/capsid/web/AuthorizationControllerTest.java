package com.gitenter.capsid.web;

import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.gitenter.capsid.service.AnonymousService;

public class AuthorizationControllerTest {
	
	private static MockMvc mockMvc;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {

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
		.andExpect(model().attributeExists("memberRegisterDTO"));
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
				"memberRegisterDTO", "email"));
		
		/*
		 * TODO:
		 * Need to check if the error message can be successfully rendered in
		 * view, otherwise still need to keep the E2E test on non-happy path until
		 * we can do some HTML test:
		 * https://docs.spring.io/spring-test-htmlunit/docs/current/reference/html5/
		 */
	}
	
	@Test
	public void testProcessRegistrationWithMissingInputs() throws Exception {
		
		mockMvc.perform(post("/register")) 
		.andExpect(view().name("authorization/register"))
		.andExpect(status().isOk())
		.andExpect(model().errorCount(4))
		.andExpect(model().attributeHasFieldErrors(
				"memberRegisterDTO", "username", "password", "displayName", "email"));
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
				"memberRegisterDTO", "username", "password", "displayName"));
	}
	
	@Test
	public void testProcessRegistrationWithInputTooLong() throws Exception {
		
		mockMvc.perform(post("/register")
				.param("username", "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx")
				.param("password", "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx")
				.param("displayName", "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx")
				.param("email", "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx@email.com"))
		.andExpect(view().name("authorization/register"))
		.andExpect(status().isOk())
		.andExpect(model().errorCount(2))
		.andExpect(model().attributeHasFieldErrors(
				"memberRegisterDTO", "username", "password"));
	}
}
