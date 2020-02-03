package com.gitenter.capsid.api;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
//import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
//import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.JacksonJsonParser;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.WebApplicationContext;

import com.gitenter.capsid.config.AuthorizationServerConfig;
import com.gitenter.capsid.config.ResourceServerConfig;
import com.gitenter.capsid.config.WebSecurityConfig;
import com.gitenter.capsid.service.OrganizationManagerService;
import com.gitenter.capsid.service.OrganizationService;
import com.gitenter.capsid.service.UserService;
import com.gitenter.capsid.web.OrganizationManagementController;
import com.gitenter.protease.dao.auth.UserRepository;
import com.gitenter.protease.domain.auth.OrganizationBean;
import com.gitenter.protease.domain.auth.OrganizationUserMapBean;
import com.gitenter.protease.domain.auth.OrganizationUserRole;
import com.gitenter.protease.domain.auth.UserBean;

@WebMvcTest(UsersController.class)
@Import({
	WebSecurityConfig.class, 
	ResourceServerConfig.class,
	AuthorizationServerConfig.class
})
public class UsersControllerTest {

//	@Autowired private WebApplicationContext wac;
//	@Autowired private FilterChainProxy springSecurityFilterChain;
//	private MockMvc mockMvc;
	@Autowired private MockMvc mockMvc;
	
	@MockBean private UserService mockUserService;
	@MockBean private UserRepository mockUserRepository;
	@MockBean private PasswordEncoder passwordEncoder;
	
	final private String username = "username";
	final private String password = "password";

	@BeforeEach
	public void setUp() throws Exception {
		
//		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac)
//		          .addFilter(springSecurityFilterChain).build();

		UserBean user = new UserBean();
		user.setUsername(username);
		user.setPasswordHash(passwordEncoder.encode(password));
		user.setDisplayName("User Name");
		user.setEmail("user@email.com");
		user.setRegisterAt(new Date(0L));
		
		List<UserBean> users = new ArrayList<UserBean>();
		users.add(user);
		
		/*
		 * `UsersDetailedServiceImpl` goes with repository (rather than
		 * service) layer.
		 */
		when(mockUserRepository.findByUsername(eq(username))).thenReturn(users);
		when(mockUserService.getUserById(eq(1))).thenReturn(user);
	}
	
	@Disabled
	public void testGetAllMembers() throws Exception {

		mockMvc.perform(get("/api/users/1")
				).andDo(print());
//		.andReturn().andResponse().getResponse().getContentAsString());
//			.andExpect(status().isOk());
	}
	
	/*
	 * TODO:
	 * Seems mock `UserRepository` doesn't work, probably because
	 * `@WebMvcTest(UsersController.class)` has no sense on auth mechanics.
	 * However, access a secured end point (general auth, not method auth)
	 * get empty 200 (just like no JWT provided).
	 */
	@Test
	public void testGetBearerToken() throws Exception {

		mockMvc.perform(get("/oauth/token")
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.with(httpBasic("gitenter-envuelope","secretpassword"))
				.content("username="+username+"&password="+password+"&grant_type=password")
				);//.andDo(print());
//		.andReturn().andResponse().getResponse().getContentAsString());
//			.andExpect(status().isOk());
	}
}
