package com.gitenter.capsid.service;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

import java.io.IOException;
import java.util.List;
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

	@Autowired private OrganizationService organizationService;
	
	@MockBean private OrganizationRepository organizationRepository;
	@MockBean private OrganizationUserMapRepository organizationUserMapRepository;
	
	private OrganizationBean organization;
	
	private UserBean manager;
	private UserBean ordinaryMember;
	
	private final Integer organizationId = 1;
	
	@BeforeEach
	public void setUp() throws Exception {

		organization = new OrganizationBean();
		organization.setName("org");
		organization.setDisplayName("Organization");
		
		/*
		 * Cannot in definition, because they should be reset after each
		 * test run.
		 */
		manager = new UserBean();
		ordinaryMember = new UserBean();
		
		manager.setUsername("manager");
		ordinaryMember.setUsername("ordinary_member");
		
		OrganizationUserMapBean.link(organization, manager, OrganizationUserRole.MANAGER);
		OrganizationUserMapBean.link(organization, ordinaryMember, OrganizationUserRole.ORDINARY_MEMBER);

		given(organizationRepository.findById(organizationId)).willReturn(Optional.of(organization));
	}

	@Test
	@WithMockUser(username="manager")
	public void testManagerCanGetManagerMaps() throws IOException {
		OrganizationBean organization = organizationService.getOrganization(1);
		List<OrganizationUserMapBean> managerMap = organizationService.getManagerMaps(organization);
		assertEquals(managerMap.size(), 1);
		assertEquals(managerMap.iterator().next().getUser(), manager);
	}
	
	@Test
	@WithMockUser(username="ordinary_member")
	public void testOrdinaryMemberCannotGetManagerMaps() throws IOException {
		OrganizationBean organization = organizationService.getOrganization(1);
		assertThrows(AccessDeniedException.class, () -> {
			organizationService.getManagerMaps(organization);
		});
	}
		
	@Test
	@WithMockUser(username="nonmember")
	public void testNonmemberCannotGetManagerMaps() throws IOException {
		OrganizationBean organization = organizationService.getOrganization(organizationId);
		assertThrows(AccessDeniedException.class, () -> {
			organizationService.getManagerMaps(organization);
		});
	}
	
	@Test
	@WithMockUser(username="manager")
	public void testManagerCanGetOrdinaryMemberMaps() throws IOException {
		OrganizationBean organization = organizationService.getOrganization(organizationId);
		List<OrganizationUserMapBean> ordinaryMemberMaps = organizationService.getOrdinaryMemberMaps(organization);
		assertEquals(ordinaryMemberMaps.size(), 1);
		assertEquals(ordinaryMemberMaps.iterator().next().getUser(), ordinaryMember);
	}
	
	@Test
	@WithMockUser(username="ordinary_member")
	public void testOrdinaryMemberCannotGetOrdinaryMemberMaps() throws IOException {
		OrganizationBean organization = organizationService.getOrganization(organizationId);
		assertThrows(AccessDeniedException.class, () -> {
			organizationService.getOrdinaryMemberMaps(organization);
		});
	}
		
	@Test
	@WithMockUser(username="nonmember")
	public void testNonmemberGetOrdinaryMemberMaps() throws IOException {
		OrganizationBean organization = organizationService.getOrganization(organizationId);
		assertThrows(AccessDeniedException.class, () -> {
			organizationService.getOrdinaryMemberMaps(organization);
		});
	}
	
	@Test
	@WithMockUser(username="manager")
	public void testManagerCanGetAllMemberMaps() throws IOException {
		OrganizationBean organization = organizationService.getOrganization(organizationId);
		List<OrganizationUserMapBean> ordinaryMemberMaps = organizationService.getAllMemberMaps(organization);
		assertEquals(ordinaryMemberMaps.size(), 2);
	}
	
	@Test
	@WithMockUser(username="ordinary_member")
	public void testOrdinaryMemberCannotGetAllMemberMaps() throws IOException {
		OrganizationBean organization = organizationService.getOrganization(organizationId);
		assertThrows(AccessDeniedException.class, () -> {
			organizationService.getAllMemberMaps(organization);
		});
	}
		
	@Test
	@WithMockUser(username="nonmember")
	public void testNonmemberGetAllMemberMaps() throws IOException {
		OrganizationBean organization = organizationService.getOrganization(organizationId);
		assertThrows(AccessDeniedException.class, () -> {
			organizationService.getAllMemberMaps(organization);
		});
	}
	
	@Test
	@WithMockUser(username="manager")
	public void testGetAllManagers() throws IOException {
		OrganizationBean organization = organizationService.getOrganization(organizationId);
		List<UserBean> members = organizationService.getAllManagers(organization);
		assertEquals(members.size(), 1);
		assertTrue(members.iterator().next().equals(manager));
	}
	
	@Test
	@WithMockUser(username="nonmember")
	public void testGetAllOrdinaryMembers() throws IOException {
		OrganizationBean organization = organizationService.getOrganization(organizationId);
		List<UserBean> members = organizationService.getAllOrdinaryMembers(organization);
		assertEquals(members.size(), 1);
		assertTrue(members.iterator().next().equals(ordinaryMember));
	}
	
	@Test
	@WithMockUser(username="nonmember")
	public void testGetAllMembers() throws IOException {
		OrganizationBean organization = organizationService.getOrganization(organizationId);
		List<UserBean> members = organizationService.getAllMembers(organization);
		assertEquals(members.size(), 2);
		assertTrue(members.iterator().next().equals(manager) || members.iterator().next().equals(ordinaryMember));
	}
}
