package com.gitenter.capsid.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.gitenter.capsid.dto.RepositoryDTO;
import com.gitenter.capsid.service.exception.InvalidOperationException;
import com.gitenter.protease.dao.auth.OrganizationUserMapRepository;
import com.gitenter.protease.dao.auth.RepositoryRepository;
import com.gitenter.protease.dao.auth.RepositoryUserMapRepository;
import com.gitenter.protease.domain.auth.OrganizationBean;
import com.gitenter.protease.domain.auth.OrganizationUserMapBean;
import com.gitenter.protease.domain.auth.OrganizationUserRole;
import com.gitenter.protease.domain.auth.RepositoryBean;
import com.gitenter.protease.domain.auth.RepositoryUserMapBean;
import com.gitenter.protease.domain.auth.RepositoryUserRole;
import com.gitenter.protease.domain.auth.UserBean;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("local")
public class RepositoryManagerServiceTest {
	
	@Autowired private RepositoryService repositoryService;
	@Autowired private RepositoryManagerService repositoryManagerService;

	@MockBean private RepositoryRepository repositoryRepository;
	@MockBean private RepositoryUserMapRepository repositoryUserMapRepository;
	@MockBean private OrganizationUserMapRepository organizationUserMapRepository;
	
	private OrganizationBean organization;
	
	private final Integer organizationId = 1;
	
	private RepositoryBean publicRepo;
	private RepositoryBean privateRepo;
	
	private final Integer publicRepoId = 1;
	private final Integer privateRepoId = 2;
	
	private UserBean projectOrganizer;
	private UserBean editor;
	private UserBean member;
	private UserBean nonmember;
	
	private OrganizationUserMapBean organizationProjectOrganizerMap;
	private OrganizationUserMapBean organizationEditorMap;
	private OrganizationUserMapBean organizationMemberMap;
	
	private RepositoryUserMapBean publicRepoProjectOrganizerMap;
	private RepositoryUserMapBean publicRepoEditorMap;
	
	private final Integer publicRepoProjectOrganizerMapId = 1;
	private final Integer publicRepoEditorMapId = 2;
	
	@SuppressWarnings("serial")
	@BeforeEach
	public void setUp() throws Exception {

		organization = new OrganizationBean();
		organization.setName("org");
		organization.setDisplayName("Organization");
		organization.setId(organizationId);
		
		publicRepo = new RepositoryBean();
		publicRepo.setName("public_repo");
		publicRepo.setDisplayName("Public Repo");
		publicRepo.setOrganization(organization);
		publicRepo.setIsPublic(true);
		publicRepo.setId(publicRepoId);
		
		privateRepo = new RepositoryBean();
		privateRepo.setName("private_repo");
		privateRepo.setDisplayName("Private Repo");
		privateRepo.setOrganization(organization);
		privateRepo.setIsPublic(false);
		privateRepo.setId(privateRepoId);
		
		organization.addRepository(publicRepo);
		organization.addRepository(privateRepo);
		
		projectOrganizer = new UserBean();
		editor = new UserBean();
		member = new UserBean();
		nonmember = new UserBean();
		
		projectOrganizer.setUsername("project_organizer");
		editor.setUsername("editor");
		member.setUsername("member");
		nonmember.setUsername("nonmember");
		
		organizationProjectOrganizerMap = OrganizationUserMapBean.link(organization, projectOrganizer, OrganizationUserRole.ORDINARY_MEMBER);
		organizationEditorMap = OrganizationUserMapBean.link(organization, editor, OrganizationUserRole.ORDINARY_MEMBER);
		organizationMemberMap = OrganizationUserMapBean.link(organization, member, OrganizationUserRole.ORDINARY_MEMBER);
		
		given(organizationUserMapRepository.fineByUserAndOrganization(
				projectOrganizer, organization)).willReturn(new ArrayList<OrganizationUserMapBean>() {{
					add(organizationProjectOrganizerMap);
				}});
		given(organizationUserMapRepository.fineByUserAndOrganization(
				editor, organization)).willReturn(new ArrayList<OrganizationUserMapBean>() {{
					add(organizationEditorMap);
				}});
		given(organizationUserMapRepository.fineByUserAndOrganization(
				member, organization)).willReturn(new ArrayList<OrganizationUserMapBean>() {{
					add(organizationMemberMap);
				}});

		publicRepoProjectOrganizerMap = RepositoryUserMapBean.link(publicRepo, projectOrganizer, RepositoryUserRole.PROJECT_ORGANIZER);
		publicRepoEditorMap = RepositoryUserMapBean.link(publicRepo, editor, RepositoryUserRole.EDITOR);
		
		given(repositoryUserMapRepository.findById(publicRepoProjectOrganizerMapId)).willReturn(Optional.of(publicRepoProjectOrganizerMap));
		given(repositoryUserMapRepository.findById(publicRepoEditorMapId)).willReturn(Optional.of(publicRepoEditorMap));
		
		RepositoryUserMapBean.link(privateRepo, projectOrganizer, RepositoryUserRole.PROJECT_ORGANIZER);
		RepositoryUserMapBean.link(privateRepo, editor, RepositoryUserRole.EDITOR);
		
		given(repositoryRepository.findById(publicRepoId)).willReturn(Optional.of(publicRepo));
		given(repositoryRepository.findById(privateRepoId)).willReturn(Optional.of(privateRepo));
		when(repositoryRepository.saveAndFlush(any(RepositoryBean.class))).thenAnswer(new Answer<RepositoryBean>() {
			@Override
			public RepositoryBean answer(InvocationOnMock invocation) throws Throwable {
		    	Object[] args = invocation.getArguments();
		    	return (RepositoryBean) args[0];
		  	}
		});
		when(repositoryUserMapRepository.saveAndFlush(any(RepositoryUserMapBean.class))).thenAnswer(new Answer<RepositoryUserMapBean>() {
			@Override
			public RepositoryUserMapBean answer(InvocationOnMock invocation) throws Throwable {
		    	Object[] args = invocation.getArguments();
		    	return (RepositoryUserMapBean) args[0];
		  	}
		});
	}

	@Test
	@WithMockUser(username="member")
	public void testOrganizationMemberCanCreateNewRepository() throws IOException, GitAPIException {
		
		assertEquals(member.getRepositories(RepositoryUserRole.PROJECT_ORGANIZER).size(), 0);
		
		RepositoryDTO repositoryDTO = new RepositoryDTO();
		repositoryDTO.setName("new_repo");
		repositoryDTO.setDisplayName("New Repository");
		repositoryDTO.setIsPublic(true);
		
		repositoryManagerService.createRepository(member, organization, repositoryDTO, false);
		
		assertEquals(member.getRepositories(RepositoryUserRole.PROJECT_ORGANIZER).size(), 1);
	}
	
	@Test
	@WithMockUser(username="nonmember")
	public void testNonmemberCannotCreateNewRepository() {
		
		RepositoryDTO repositoryDTO = new RepositoryDTO();
		repositoryDTO.setName("new_repo");
		repositoryDTO.setDisplayName("New Repository");
		repositoryDTO.setIsPublic(true);
		
		assertThrows(AccessDeniedException.class, () -> {
			repositoryManagerService.createRepository(nonmember, organization, repositoryDTO, false);
		});
	}
	
	@Test
	@WithMockUser(username="project_organizer")
	public void testProjectOrganizerCanUpdateRepository() throws IOException {
			
		RepositoryDTO repositoryDTO = new RepositoryDTO();
		repositoryDTO.setName("public_repo"); // cannot be changed
		repositoryDTO.setDisplayName("Public Repository Update Name");
		repositoryDTO.setIsPublic(false);
		
		repositoryManagerService.updateRepository(publicRepo, repositoryDTO);
		
		assertEquals(publicRepo.getDisplayName(), "Public Repository Update Name");
		assertEquals(publicRepo.getIsPublic(), false);
	}
	
	@Test
	@WithMockUser(username="editor")
	public void testEditorCannotUpdateRepository() {
			
		RepositoryDTO repositoryDTO = new RepositoryDTO();
		repositoryDTO.setName("public_repo");
		repositoryDTO.setDisplayName("Public Repository Update Name");
		repositoryDTO.setIsPublic(false);
		
		assertThrows(AccessDeniedException.class, () -> {
			repositoryManagerService.updateRepository(publicRepo, repositoryDTO);
		});
	}
	
	@Test
	@WithMockUser(username="project_organizer")
	public void testRepositoryNameCannotBeChanged() {
			
		RepositoryDTO repositoryDTO = new RepositoryDTO();
		repositoryDTO.setName("public_repo_different_name");
		repositoryDTO.setDisplayName("Public Repository Update Name");
		repositoryDTO.setIsPublic(false);
		
		assertThrows(InvalidOperationException.class, () -> {
			repositoryManagerService.updateRepository(publicRepo, repositoryDTO);
		});
	}
	
	@Test
	@WithMockUser(username="project_organizer")
	public void testProjectOrganizerCanAddCollebrator() throws IOException {
		
		assertEquals(repositoryService.getEditors(publicRepo).size(), 1);
		repositoryManagerService.addCollaborator(publicRepo, member, "EDITOR");
		assertEquals(repositoryService.getEditors(publicRepo).size(), 2);
	}
	
	@Test
	@WithMockUser(username="editor")
	public void testEditorCannotAddCollebrator() throws IOException {
		
		assertThrows(AccessDeniedException.class, () -> {
			repositoryManagerService.addCollaborator(publicRepo, member, "EDITOR");
		});
	}
	
	@Test
	@WithMockUser(username="project_organizer")
	public void testProjectOrganizerCanRemoveCollebrator() throws IOException {
		
		Authentication mockAuthentication = Mockito.mock(Authentication.class);
		Mockito.when(mockAuthentication.getName()).thenReturn("project_organizer");
		
		assertEquals(repositoryService.getEditors(publicRepo).size(), 1);
		repositoryManagerService.removeCollaborator(mockAuthentication, publicRepo, publicRepoEditorMapId);
		assertEquals(repositoryService.getEditors(publicRepo).size(), 0);
	}
	
	@Test
	@WithMockUser(username="project_organizer")
	public void testProjectOrganizerCannotRemoveHerselfAsCollebrator() {
		
		Authentication mockAuthentication = Mockito.mock(Authentication.class);
		Mockito.when(mockAuthentication.getName()).thenReturn("project_organizer");
		
		assertThrows(InvalidOperationException.class, () -> {
			repositoryManagerService.removeCollaborator(mockAuthentication, publicRepo, publicRepoProjectOrganizerMapId);
		});	
	}
	
	@Test
	@WithMockUser(username="editor")
	public void testEditorCanRemoveCollebrator() {
		
		Authentication mockAuthentication = Mockito.mock(Authentication.class);
		Mockito.when(mockAuthentication.getName()).thenReturn("editor");
		
		assertThrows(AccessDeniedException.class, () -> {
			repositoryManagerService.removeCollaborator(mockAuthentication, publicRepo, publicRepoEditorMapId);
		});
	}
	
	@Test
	@WithMockUser(username="project_organizer")
	public void testProjectOrganizerCanDeleteRepository() throws IOException, GitAPIException {
		repositoryManagerService.deleteRepository(publicRepo);
	}
	
	@Test
	@WithMockUser(username="editor")
	public void testEditorCannotDeleteRepository() throws IOException, GitAPIException {
		assertThrows(AccessDeniedException.class, () -> {
			repositoryManagerService.deleteRepository(publicRepo);
		});
	}
}
