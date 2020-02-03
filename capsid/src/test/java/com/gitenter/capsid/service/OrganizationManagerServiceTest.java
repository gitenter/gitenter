package com.gitenter.capsid.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

import java.io.IOException;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.gitenter.capsid.dto.OrganizationDTO;
import com.gitenter.capsid.service.exception.InvalidOperationException;
import com.gitenter.protease.dao.auth.OrganizationRepository;
import com.gitenter.protease.dao.auth.OrganizationUserMapRepository;
import com.gitenter.protease.domain.auth.OrganizationBean;
import com.gitenter.protease.domain.auth.OrganizationUserMapBean;
import com.gitenter.protease.domain.auth.OrganizationUserRole;
import com.gitenter.protease.domain.auth.UserBean;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("local")
public class OrganizationManagerServiceTest {

	@Autowired private OrganizationService organizationService;
	@Autowired private OrganizationManagerService organizationManagerService;
	
	@MockBean private OrganizationRepository organizationRepository;
	@MockBean private OrganizationUserMapRepository organizationUserMapRepository;
	
	private OrganizationBean organization;
	
	private UserBean manager;
	private UserBean ordinaryMember;
	private UserBean nonmember;
	
	private final Integer organizationId = 1;
	private final Integer managerMapId = 1;
	private final Integer ordinaryMemberMapId = 2;
	
	@BeforeEach
	public void setUp() throws Exception {

		organization = new OrganizationBean();
		organization.setName("org");
		organization.setDisplayName("Organization");
		organization.setId(organizationId);
		
		manager = new UserBean();
		ordinaryMember = new UserBean();
		nonmember = new UserBean();
		
		manager.setUsername("manager");
		ordinaryMember.setUsername("ordinary_member");
		nonmember.setUsername("nonmember");
		
		OrganizationUserMapBean managerMap = OrganizationUserMapBean.link(
				organization, manager, OrganizationUserRole.MANAGER);
		OrganizationUserMapBean ordinaryMemberMap = OrganizationUserMapBean.link(
				organization, ordinaryMember, OrganizationUserRole.ORDINARY_MEMBER);
		
		given(organizationUserMapRepository.findById(managerMapId)).willReturn(Optional.of(managerMap));
		given(organizationUserMapRepository.findById(ordinaryMemberMapId)).willReturn(Optional.of(ordinaryMemberMap));
	}
	
	@Test
	@WithMockUser(username="nonmember")
	public void testEverybodyCanCreateOrganization() throws IOException {
		
		assertEquals(nonmember.getOrganizations(OrganizationUserRole.MANAGER).size(), 0);
		assertEquals(nonmember.getOrganizations(OrganizationUserRole.ORDINARY_MEMBER).size(), 0);
		
		OrganizationDTO organizationDTO = new OrganizationDTO();
		organizationDTO.setName("new_org");
		organizationDTO.setDisplayName("New Organization");
		
		organizationManagerService.createOrganization(nonmember, organizationDTO);
		
		assertEquals(nonmember.getOrganizations(OrganizationUserRole.MANAGER).size(), 1);
		assertEquals(nonmember.getOrganizations(OrganizationUserRole.ORDINARY_MEMBER).size(), 0);
	}

	@Test
	@WithMockUser(username="manager")
	public void testManagerCanUpdateOrganization() throws IOException {
		
		OrganizationDTO organizationDTO = new OrganizationDTO();
		organizationDTO.setName("org");
		organizationDTO.setDisplayName("Organization New Name");
		
		organizationManagerService.updateOrganization(organization, organizationDTO);
		
		assertEquals(organization.getDisplayName(), "Organization New Name");
	}
	
	@Test
	@WithMockUser(username="ordinary_member")
	public void testOrdinaryMemberCannotUpdateOrganization() throws IOException {
		
		OrganizationDTO organizationDTO = new OrganizationDTO();
		assertThrows(AccessDeniedException.class, () -> {
			organizationManagerService.updateOrganization(organization, organizationDTO);
		});
	}
	
	@Test
	@WithMockUser(username="manager")
	public void testManagerCanAddOrganizationMember() throws IOException {
		
		assertEquals(organizationService.getAllMembers(organization).size(), 2);
		organizationManagerService.addOrganizationOrdinaryMember(organization, nonmember);
		assertEquals(organizationService.getAllMembers(organization).size(), 3);
	}
	
	@Test
	@WithMockUser(username="ordinary_member")
	public void testOrdinaryMemberCannotAddOrganizationMember() throws IOException {
		
		assertThrows(AccessDeniedException.class, () -> {
			organizationManagerService.addOrganizationOrdinaryMember(organization, nonmember);
		});
	}
	
	@Test
	@WithMockUser(username="manager")
	public void testManagerCanRemoveOrganizationMember() throws IOException {
		
		Authentication mockAuthentication = Mockito.mock(Authentication.class);
		Mockito.when(mockAuthentication.getName()).thenReturn("manager");
		
		assertEquals(organizationService.getAllMembers(organization).size(), 2);
		organizationManagerService.removeOrganizationMember(mockAuthentication, organization, ordinaryMemberMapId);
		assertEquals(organizationService.getAllMembers(organization).size(), 1);
	}
	
	@Test
	@WithMockUser(username="manager")
	public void testManagerCannotRemoveThemselvesAsOrganizationMember() throws IOException {
		
		Authentication mockAuthentication = Mockito.mock(Authentication.class);
		Mockito.when(mockAuthentication.getName()).thenReturn("manager");
		
		assertThrows(InvalidOperationException.class, () -> {
			organizationManagerService.removeOrganizationMember(mockAuthentication, organization, managerMapId);
		});
	}
	
	@Test
	@WithMockUser(username="ordinary_member")
	public void testOrdinaryMemberCannotRemoveOrganizationMember() throws IOException {
		
		Authentication mockAuthentication = Mockito.mock(Authentication.class);
		Mockito.when(mockAuthentication.getName()).thenReturn("ordinary_member");
		
		/*
		 * TODO:
		 * Should it be the case she can remove herself?
		 */
		assertThrows(AccessDeniedException.class, () -> {
			organizationManagerService.removeOrganizationMember(mockAuthentication, organization, ordinaryMemberMapId);
		});
	}
	
	@Test
	@WithMockUser(username="manager")
	public void testManagerCanAddAndRemoveOrganizationManagerButCannotRemoveHerselfAsManager() throws IOException {
		
		Authentication mockAuthentication = Mockito.mock(Authentication.class);
		Mockito.when(mockAuthentication.getName()).thenReturn("manager");

		assertEquals(organizationService.getManagerMaps(organization).size(), 1);
		organizationManagerService.promoteOrganizationManager(organization, ordinaryMemberMapId);
		assertEquals(organizationService.getManagerMaps(organization).size(), 2);
		
		organizationManagerService.demoteOrganizationManager(mockAuthentication, organization, ordinaryMemberMapId);
		assertEquals(organizationService.getManagerMaps(organization).size(), 1);
		
		assertThrows(InvalidOperationException.class, () -> {
			organizationManagerService.demoteOrganizationManager(mockAuthentication, organization, managerMapId);
		});
	}
	
	@Test
	@WithMockUser(username="ordinary_member")
	public void testOrdinaryMemberCannotAddAndRemoveOrganizationManager() throws IOException {
		
		Authentication mockAuthentication = Mockito.mock(Authentication.class);
		Mockito.when(mockAuthentication.getName()).thenReturn("ordinary_member");

		assertThrows(AccessDeniedException.class, () -> {
			organizationManagerService.promoteOrganizationManager(organization, ordinaryMemberMapId);
		});
		
		assertThrows(AccessDeniedException.class, () -> {
			organizationManagerService.demoteOrganizationManager(mockAuthentication, organization, ordinaryMemberMapId);
		});
	}
	
	@Test
	@WithMockUser(username="manager")
	public void testManagerCanDeleteOrganization() throws IOException {
		organizationManagerService.deleteOrganization(organization);
	}
	
	@Test
	@WithMockUser(username="ordinary_member")
	public void testOrdinaryMemberCannotDeleteOrganization() throws IOException {

		assertThrows(AccessDeniedException.class, () -> {
			organizationManagerService.deleteOrganization(organization);
		});
	}
}
