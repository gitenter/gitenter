package com.gitenter.capsid.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

import java.io.IOException;
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

import com.gitenter.capsid.dto.OrganizationDTO;
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
	
	@BeforeEach
	public void setUp() throws Exception {

		organization = new OrganizationBean();
		organization.setName("org");
		organization.setDisplayName("Organization");
		
		manager = new UserBean();
		ordinaryMember = new UserBean();
		nonmember = new UserBean();
		
		manager.setUsername("manager");
		ordinaryMember.setUsername("ordinary_member");
		nonmember.setUsername("nonmember");
		
		OrganizationUserMapBean.link(organization, manager, OrganizationUserRole.MANAGER);
		OrganizationUserMapBean.link(organization, ordinaryMember, OrganizationUserRole.ORDINARY_MEMBER);
		
		Optional<OrganizationBean> organization_or_null = Optional.of(organization);
		given(organizationRepository.findById(1)).willReturn(organization_or_null);
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
	
	/*
	 * TODO:
	 * `removeOrganizationMember`. Not as easy as `addOrganizationMember` as MapId is needed.
	 */
}
