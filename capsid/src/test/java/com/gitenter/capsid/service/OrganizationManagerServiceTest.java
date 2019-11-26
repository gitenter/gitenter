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

	@MockBean private OrganizationRepository organizationRepository;
	@MockBean private OrganizationUserMapRepository organizationUserMapRepository;
	
	@Autowired private OrganizationService organizationService;
	@Autowired private OrganizationManagerService organizationManagerService;
	
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
		
		OrganizationUserMapBean managerMap = OrganizationUserMapBean.link(organization, manager, OrganizationUserRole.MANAGER);
		OrganizationUserMapBean ordinaryMemberMap = OrganizationUserMapBean.link(organization, ordinaryMember, OrganizationUserRole.ORDINARY_MEMBER);
		
		Optional<OrganizationUserMapBean> managerMapOrNull = Optional.of(managerMap);
		Optional<OrganizationUserMapBean> ordinaryMemberMapOrNull = Optional.of(ordinaryMemberMap);
		
		given(organizationUserMapRepository.findById(managerMapId)).willReturn(managerMapOrNull);
		given(organizationUserMapRepository.findById(ordinaryMemberMapId)).willReturn(ordinaryMemberMapOrNull);
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
		organizationManagerService.addOrganizationMember(organization, nonmember);
		assertEquals(organizationService.getAllMembers(organization).size(), 3);
	}
	
	@Test
	@WithMockUser(username="ordinary_member")
	public void testOrdinaryMemberCannotAddOrganizationMember() throws IOException {
		
		assertThrows(AccessDeniedException.class, () -> {
			organizationManagerService.addOrganizationMember(organization, nonmember);
		});
	}
	
	@Test
	@WithMockUser(username="manager")
	public void testManagerCanRemoveOrganizationMember() throws IOException {
		
		assertEquals(organizationService.getAllMembers(organization).size(), 2);
		organizationManagerService.removeOrganizationMember(organization, ordinaryMemberMapId);
		assertEquals(organizationService.getAllMembers(organization).size(), 1);
	}
	
	@Test
	@WithMockUser(username="ordinary_member")
	public void testOrdinaryMemberCannotRemoveOrganizationMember() throws IOException {
		
		/*
		 * TODO:
		 * Should it be the case she can remove herself?
		 */
		assertThrows(AccessDeniedException.class, () -> {
			organizationManagerService.removeOrganizationMember(organization, ordinaryMemberMapId);
		});
	}
	
	@Test
	@WithMockUser(username="manager")
	public void testManagerCanAddAndRemoveOrganizationManagerButCannotRemoveHerselfAsManager() throws IOException {
		
		Authentication mockAuthentication = Mockito.mock(Authentication.class);
		Mockito.when(mockAuthentication.getName()).thenReturn("manager");

		assertEquals(organizationService.getManagerMaps(organization).size(), 1);
		organizationManagerService.addOrganizationManager(organization, ordinaryMemberMapId);
		
		assertEquals(organizationService.getManagerMaps(organization).size(), 2);
		
		organizationManagerService.removeOrganizationManager(mockAuthentication, organization, ordinaryMemberMapId);
		assertEquals(organizationService.getManagerMaps(organization).size(), 1);
		
		assertThrows(InvalidOperationException.class, () -> {
			organizationManagerService.removeOrganizationManager(mockAuthentication, organization, managerMapId);
		});
	}
	
	@Test
	@WithMockUser(username="ordinary_member")
	public void testOrdinaryMemberCannotAddAndRemoveOrganizationManager() throws IOException {
		
		Authentication mockAuthentication = Mockito.mock(Authentication.class);
		Mockito.when(mockAuthentication.getName()).thenReturn("ordinary_member");

		assertThrows(AccessDeniedException.class, () -> {
			organizationManagerService.addOrganizationManager(organization, ordinaryMemberMapId);
		});
		
		assertThrows(AccessDeniedException.class, () -> {
			organizationManagerService.removeOrganizationManager(mockAuthentication, organization, ordinaryMemberMapId);
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
