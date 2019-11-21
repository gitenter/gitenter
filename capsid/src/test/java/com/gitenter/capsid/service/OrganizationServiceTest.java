package com.gitenter.capsid.service;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.gitenter.protease.dao.auth.OrganizationRepository;
import com.gitenter.protease.dao.auth.OrganizationUserMapRepository;
import com.gitenter.protease.domain.auth.OrganizationBean;
import com.gitenter.protease.domain.auth.OrganizationUserMapBean;
import com.gitenter.protease.domain.auth.OrganizationUserRole;
import com.gitenter.protease.domain.auth.UserBean;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("local")
public class OrganizationServiceTest {

	@MockBean private OrganizationRepository organizationRepository;
	@MockBean private OrganizationUserMapRepository organizationUserMapRepository;
	
	@Autowired private OrganizationService organizationService;
	
	private OrganizationBean organization;
	private UserBean manager = new UserBean();
	private UserBean ordinaryMember = new UserBean();
	
	@BeforeEach
	public void setUp() throws Exception {

		organization = new OrganizationBean();
		organization.setName("org");
		organization.setDisplayName("Organization");
		
		manager = new UserBean();
		manager.setUsername("manager");
		ordinaryMember = new UserBean();
		ordinaryMember.setUsername("ordinary_member");
		
		OrganizationUserMapBean managerMap = new OrganizationUserMapBean();
		managerMap.setOrganization(organization);
		managerMap.setUser(manager);
		managerMap.setRole(OrganizationUserRole.MANAGER);
		
		OrganizationUserMapBean memberMap = new OrganizationUserMapBean();
		memberMap.setOrganization(organization);
		memberMap.setUser(ordinaryMember);
		memberMap.setRole(OrganizationUserRole.ORDINARY_MEMBER);
		
		organization.setOrganizationUserMaps(Arrays.asList(managerMap, memberMap));
		manager.setOrganizationUserMaps(Arrays.asList(managerMap));
		ordinaryMember.setOrganizationUserMaps(Arrays.asList(memberMap));
		
		Optional<OrganizationBean> organization_or_null = Optional.of(organization);

		given(organizationRepository.findById(1)).willReturn(organization_or_null);
	}
	

	@Test
	@WithMockUser(username="manager")
	public void testGetManagerMapsFromManager() throws IOException {
		OrganizationBean organization = organizationService.getOrganization(1);
		Collection<OrganizationUserMapBean> managerMap = organizationService.getManagerMaps(organization);
		assertEquals(managerMap.size(), 1);
		assertEquals(managerMap.iterator().next().getUser(), manager);
	}
	
	@Test
	@WithMockUser(username="ordinary_member")
	public void testGetManagerMapsFromMember() throws IOException {
		OrganizationBean organization = organizationService.getOrganization(1);
		assertThrows(AccessDeniedException.class, () -> {
			organizationService.getManagerMaps(organization);
		});
	}
		
	@Test
	@WithMockUser(username="nonmember")
	public void testGetManagerMapsFromOutsider() throws IOException {
		OrganizationBean organization = organizationService.getOrganization(1);
		assertThrows(AccessDeniedException.class, () -> {
			organizationService.getManagerMaps(organization);
		});
	}
	
	@Test
	@WithMockUser(username="manager")
	public void testGetOrdinaryMemberMapsFromManager() throws IOException {
		OrganizationBean organization = organizationService.getOrganization(1);
		Collection<OrganizationUserMapBean> ordinaryMemberMaps = organizationService.getOrdinaryMemberMaps(organization);
		assertEquals(ordinaryMemberMaps.size(), 1);
		assertEquals(ordinaryMemberMaps.iterator().next().getUser(), ordinaryMember);
	}
	
	@Test
	@WithMockUser(username="ordinary_member")
	public void testGetOrdinaryMemberMapsFromMember() throws IOException {
		OrganizationBean organization = organizationService.getOrganization(1);
		assertThrows(AccessDeniedException.class, () -> {
			organizationService.getOrdinaryMemberMaps(organization);
		});
	}
		
	@Test
	@WithMockUser(username="nonmember")
	public void testGetOrdinaryMemberMapsFromOutsider() throws IOException {
		OrganizationBean organization = organizationService.getOrganization(1);
		assertThrows(AccessDeniedException.class, () -> {
			organizationService.getOrdinaryMemberMaps(organization);
		});
	}
	
	@Test
	@WithMockUser(username="nonmember")
	public void testGetAllMembers() throws IOException {
		OrganizationBean organization = organizationService.getOrganization(1);
		Collection<UserBean> members = organizationService.getAllMembers(organization);
		assertEquals(members.size(), 2);
		assertTrue(members.iterator().next().equals(manager) || members.iterator().next().equals(ordinaryMember));
	}
}
