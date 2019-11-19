package com.gitenter.capsid.web;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
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

import com.gitenter.capsid.service.PersonService;
import com.gitenter.capsid.service.OrganizationService;
import com.gitenter.capsid.service.RepositoryManagerService;
import com.gitenter.capsid.service.RepositoryService;
import com.gitenter.protease.domain.auth.OrganizationBean;

public class RepositoryManagementControllerTest {
	
	private MockMvc mockMvc;
	
	private PersonService mockPersonService;
	private OrganizationService mockOrganizationService;
	private RepositoryService mockRepositoryService;
	private RepositoryManagerService mockRepositoryManagerService;
	
	@BeforeEach
	public void setUp() throws Exception {

		mockPersonService = mock(PersonService.class);
		mockOrganizationService = mock(OrganizationService.class);
		mockRepositoryService = mock(RepositoryService.class);
		mockRepositoryManagerService = mock(RepositoryManagerService.class);
		RepositoryManagementController controller = new RepositoryManagementController(
				mockPersonService, mockOrganizationService, mockRepositoryService, mockRepositoryManagerService);
		mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
		
		OrganizationBean organization = new OrganizationBean();
		when(mockOrganizationService.getOrganization(eq(1))).thenReturn(organization);
	}
	
	@Test
	public void testCreateRepositoryWithValidInput() throws Exception {
		
		mockMvc.perform(post("/organizations/1/repositories/create")
				.param("name", "org")
				.param("displayName", "A Organization")
				.param("isPublic", "true")
				.param("include_setup_files", "true"))
		.andExpect(redirectedUrl("/organizations/1"));
	}
	
	@Test
	public void testCreateRepositoryWithInputToShort() throws Exception {
		
		mockMvc.perform(post("/organizations/1/repositories/create")
				.param("name", "o")
				.param("displayName", "O")
				.param("isPublic", "true")
				.param("include_setup_files", "true"))
		.andExpect(view().name("repository-management/create"))
		.andExpect(status().isOk())
		.andExpect(model().errorCount(2))
		.andExpect(model().attributeHasFieldErrors(
				"repositoryDTO", "name", "displayName"))
		.andReturn().getResponse().getContentAsString().contains("size must be between");
	}
	
	@Test
	public void testCreateRepositoryWithInputToLong() throws Exception {
		
		mockMvc.perform(post("/organizations/1/repositories/create")
				.param("name", StringUtils.repeat('x', 17))
				.param("displayName",StringUtils.repeat('x', 65))
				.param("isPublic", "true")
				.param("include_setup_files", "true"))
		.andExpect(status().isOk())
		.andExpect(model().errorCount(2))
		.andExpect(model().attributeHasFieldErrors(
				"repositoryDTO", "name", "displayName"))
		.andReturn().getResponse().getContentAsString().contains("size must be between");
	}
}
