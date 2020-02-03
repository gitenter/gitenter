package com.gitenter.capsid.api;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
//import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
//import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.JacksonJsonParser;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.gitenter.capsid.config.WebSecurityConfig;
import com.gitenter.capsid.service.OrganizationManagerService;
import com.gitenter.capsid.service.OrganizationService;
import com.gitenter.capsid.service.UserService;
import com.gitenter.capsid.web.OrganizationManagementController;
import com.gitenter.protease.domain.auth.OrganizationBean;
import com.gitenter.protease.domain.auth.OrganizationUserMapBean;
import com.gitenter.protease.domain.auth.OrganizationUserRole;
import com.gitenter.protease.domain.auth.UserBean;

@WebMvcTest(OrganizationsController.class)
public class OrganizationsControllerTest {

	@Autowired 
	private MockMvc mockMvc;
	
	@MockBean private UserService mockUserService;
	@MockBean private OrganizationService mockOrganizationService;
	@MockBean private OrganizationManagerService mockOrganizationManagerService;
	
	@BeforeEach
	public void setUp() throws Exception {
		
		OrganizationBean organization = new OrganizationBean();		
		UserBean manager = new UserBean();
		UserBean ordinaryMember = new UserBean();
		OrganizationUserMapBean managerMap = OrganizationUserMapBean.link(
				organization, manager, OrganizationUserRole.MANAGER);
		OrganizationUserMapBean ordinaryMemberMap = OrganizationUserMapBean.link(
				organization, ordinaryMember, OrganizationUserRole.ORDINARY_MEMBER);
		List<UserBean> allMembers = new ArrayList<UserBean>();
		allMembers.add(manager);
		allMembers.add(ordinaryMember);
		List<OrganizationUserMapBean> allMemberMaps = new ArrayList<OrganizationUserMapBean>();
		allMemberMaps.add(managerMap);
		allMemberMaps.add(ordinaryMemberMap);
		
		when(mockOrganizationService.getOrganization(eq(1))).thenReturn(organization);
		when(mockOrganizationService.getAllMemberMaps(eq(organization))).thenReturn(
				allMemberMaps);
		when(mockOrganizationService.getAllMembers(eq(organization))).thenReturn(
				allMembers);
	}
	
	/*
	 * TODO:
	 * Unfortunately, right now there's no easy way to mock JWT authorization.
	 * Details see GitHub open issue:
	 * https://github.com/spring-projects/spring-security/issues/6557
	 * An example which is not workong on my side:
	 * https://github.com/spring-projects/spring-security/blob/master/samples/boot/oauth2resourceserver/src/test/java/sample/OAuth2ResourceServerControllerTests.java
	 */
	@Test
	public void testGetAllMembers() throws Exception {

		mockMvc.perform(get("/api/organizations/1/members")
				);//.andDo(print());
//		.andReturn().andResponse().getResponse().getContentAsString());
//			.andExpect(status().isOk());
	}
}
