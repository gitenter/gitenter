package com.gitenter.capsid.web;

import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.gitenter.capsid.service.MemberService;
import com.gitenter.capsid.service.OrganizationManagerService;
import com.gitenter.capsid.service.OrganizationService;

public class OrganizationManagementControllerTest {
	
	private MockMvc mockMvc;
	
	private MemberService mockMemberService;
	private OrganizationService mockOrganizationService;
	private OrganizationManagerService mockOrganizationManagerService;
	
	@BeforeEach
	public void setUp() throws Exception {

		mockMemberService = mock(MemberService.class);
		mockOrganizationService = mock(OrganizationService.class);
		mockOrganizationManagerService = mock(OrganizationManagerService.class);
		OrganizationManagementController controller = new OrganizationManagementController(
				mockMemberService, mockOrganizationService, mockOrganizationManagerService);
		mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
	}
	
	@Test
	public void testCreateOrganizationWithValidInput() throws Exception {
		
		mockMvc.perform(post("/organizations/create")
				.param("name", "org")
				.param("displayName", "A Organization"))
		.andExpect(redirectedUrl("/"));
	}
	
	@Test
	public void testCreateOrganizationWithInputTooShort() throws Exception {
		
		mockMvc.perform(post("/organizations/create")
				.param("name", "o")
				.param("displayName", "O"))
		.andExpect(view().name("organization-management/create"))
		.andExpect(status().isOk())
		.andExpect(model().errorCount(2))
		.andExpect(model().attributeHasFieldErrors(
				"organizationDTO", "name", "displayName"))
		.andReturn().getResponse().getContentAsString().contains("size must be between");
	}
	
	@Test
	public void testCreateOrganizationWithInputTooLong() throws Exception {
		
		mockMvc.perform(post("/organizations/create")
				.param("name", StringUtils.repeat('x', 17))
				.param("displayName",StringUtils.repeat('x', 65)))
		.andExpect(view().name("organization-management/create"))
		.andExpect(status().isOk())
		.andExpect(model().errorCount(2))
		.andExpect(model().attributeHasFieldErrors(
				"organizationDTO", "name", "displayName"))
		.andReturn().getResponse().getContentAsString().contains("size must be between");
	}
}
