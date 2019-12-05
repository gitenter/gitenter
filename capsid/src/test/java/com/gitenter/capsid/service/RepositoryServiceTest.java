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

import com.gitenter.protease.dao.auth.RepositoryRepository;
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
public class RepositoryServiceTest {
	
	@Autowired private RepositoryService repositoryService;

	@MockBean private RepositoryRepository repositoryRepository;
	
	private OrganizationBean organization;
	
	private RepositoryBean publicRepo;
	private RepositoryBean privateRepo;
	
	private UserBean projectOrganizer;
	private UserBean editor;
	private UserBean member;
	private UserBean nonmember;
	
	private final Integer organizationId = 1;
	private final Integer publicRepoId = 1;
	private final Integer privateRepoId = 2;
	
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
		
		OrganizationUserMapBean.link(organization, projectOrganizer, OrganizationUserRole.ORDINARY_MEMBER);
		OrganizationUserMapBean.link(organization, editor, OrganizationUserRole.ORDINARY_MEMBER);
		OrganizationUserMapBean.link(organization, member, OrganizationUserRole.ORDINARY_MEMBER);
	
		RepositoryUserMapBean.link(publicRepo, projectOrganizer, RepositoryUserRole.PROJECT_ORGANIZER);
		RepositoryUserMapBean.link(publicRepo, editor, RepositoryUserRole.EDITOR);
		
		given(repositoryRepository.findById(publicRepoId)).willReturn(Optional.of(publicRepo));
		given(repositoryRepository.findById(privateRepoId)).willReturn(Optional.of(privateRepo));
	}

	@Test
	@WithMockUser(username="project_organizer")
	public void testProjectOrganizerCanAccessPublicAndPrivateRepo() throws IOException {
		
		RepositoryBean publicRepoCopy = repositoryService.getRepository(publicRepoId);
		assertEquals(publicRepoCopy.getName(), "public_repo");
		
		RepositoryBean privateRepoCopy = repositoryService.getRepository(privateRepoId);
		assertEquals(privateRepoCopy.getName(), "private_repo");
	}
	
	@Test
	@WithMockUser(username="editor")
	public void testEditorCanAccessPublicAndPrivateRepo() throws IOException {
		
		RepositoryBean publicRepoCopy = repositoryService.getRepository(publicRepoId);
		assertEquals(publicRepoCopy.getName(), "public_repo");
		
		RepositoryBean privateRepoCopy = repositoryService.getRepository(privateRepoId);
		assertEquals(privateRepoCopy.getName(), "private_repo");
	}
	
	@Test
	@WithMockUser(username="member")
	public void testOrganizationMemberCanAccessPublicAndPrivateRepo() throws IOException {
		
		RepositoryBean publicRepoCopy = repositoryService.getRepository(publicRepoId);
		assertEquals(publicRepoCopy.getName(), "public_repo");
		
		RepositoryBean privateRepoCopy = repositoryService.getRepository(privateRepoId);
		assertEquals(privateRepoCopy.getName(), "private_repo");
	}
	
	@Test
	@WithMockUser(username="nonmember")
	public void testNonmemberCanAccessPublicButCannotAccessPrivateRepo() throws IOException {
		
		RepositoryBean publicRepoCopy = repositoryService.getRepository(publicRepoId);
		assertEquals(publicRepoCopy.getName(), "public_repo");
		
		assertThrows(AccessDeniedException.class, () -> {
			repositoryService.getRepository(privateRepoId);
		});
	}
}
