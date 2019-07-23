package com.gitenter.protease.dao.auth;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;

import com.gitenter.protease.ProteaseConfig;
import com.gitenter.protease.config.bean.GitSource;
import com.gitenter.protease.domain.auth.OrganizationBean;
import com.gitenter.protease.domain.auth.RepositoryBean;
import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseTearDown;
import com.github.springtestdbunit.annotation.DbUnitConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles(profiles = "wildcard")
@ContextConfiguration(classes=ProteaseConfig.class)
@TestExecutionListeners({
	DependencyInjectionTestExecutionListener.class,
	DirtiesContextTestExecutionListener.class,
	TransactionalTestExecutionListener.class,
	DbUnitTestExecutionListener.class })
@DbUnitConfiguration(databaseConnection={
		"schemaAuthDatabaseConnection",
		"schemaGitDatabaseConnection",
		"schemaTraceabilityDatabaseConnection",
		"schemaReviewDatabaseConnection"})
public class RepositoryRepositoryTestWildCardProfile {
	
	@ClassRule public final static TemporaryFolder tempFolder = new TemporaryFolder();
	
	/*
	 * Cannot define this bean in `TestGitSourceConfig`, because (starting from JUnit 4.11)
	 * `TemporaryFolder` needs to be used together with `@Rule`, but we cannot define
	 * `@Rule` in a non-unittest class.
	 */
	@Configuration
	static class Config {
		
		@Profile("wildcard")
		@Bean
		public GitSource wildcardGitSource() throws IOException {
			
			GitSource gitSource = mock(GitSource.class);
			when(gitSource.getBareRepositoryDirectory(any(String.class), any(String.class)))
				.thenReturn(tempFolder.newFolder("wildcard.git"));
			
			return gitSource;
		}
	}
	
	@Autowired RepositoryRepository repositoryRepository;
	@Autowired OrganizationRepository organizationRepository;
	
	@Autowired private GitSource gitSource;
	
	/*
	 * This particular test needs a different (wildcard) profile which is backed by
	 * `TemporaryFolder`. If uses `minimal` profile, it will either cannot find mock
	 * of `gitSource`, or accidentally delete `repo/minimal.git` and fail the other
	 * tests.
	 */
	@Test
	@DatabaseTearDown
	public void testCreateRepositoryGetGitFolderCreated() throws IOException, GitAPIException {
		
		OrganizationBean organization = new OrganizationBean();
		organization.setName("irrelevant_organization");
		organization.setDisplayName("Irrelavent Organization");
		organizationRepository.saveAndFlush(organization);
		
		RepositoryBean repository = new RepositoryBean();
		repository.setOrganization(organization);
		repository.setName("irrelevant_repo");
		repository.setDisplayName("Irrelavent Repository");
		repository.setIsPublic(true);
		
		File repositoryDirectory = gitSource.getBareRepositoryDirectory(
				organization.getName(),
				repository.getName());
		
		repositoryRepository.saveAndFlush(repository);
		assertTrue(repositoryDirectory.exists());
		
		repositoryRepository.delete(repository);
		assertFalse(repositoryDirectory.exists());
	}
}
