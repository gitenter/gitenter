package com.gitenter.protease.dao.auth;

import java.io.IOException;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;

import com.gitenter.protease.ProteaseConfig;
import com.gitenter.protease.annotation.DbUnitMinimalDataSetup;
import com.gitenter.protease.dao.exception.RepositoryNameNotUniqueException;
import com.gitenter.protease.domain.auth.OrganizationBean;
import com.gitenter.protease.domain.auth.RepositoryBean;
import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseTearDown;
import com.github.springtestdbunit.annotation.DbUnitConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles(profiles = "minimal")
@ContextConfiguration(classes=ProteaseConfig.class)
@TestExecutionListeners({
	DependencyInjectionTestExecutionListener.class,
	DirtiesContextTestExecutionListener.class,
	TransactionalTestExecutionListener.class,
	DbUnitTestExecutionListener.class })
@DbUnitConfiguration(databaseConnection={"schemaAuthDatabaseConnection", "schemaGitDatabaseConnection", "schemaReviewDatabaseConnection"})
public class RepositoryRepositoryTest {
	
	@Autowired RepositoryRepository repositoryRepository;

	@Autowired OrganizationRepository organizationRepository;
	
	@Test(expected = RepositoryNameNotUniqueException.class)
	@DbUnitMinimalDataSetup
	@DatabaseTearDown
	public void testCannotSaveTwoRepositoryWithTheSameName() throws IOException, GitAPIException, RepositoryNameNotUniqueException {
		
		RepositoryBean existingRepository = repositoryRepository.findById(1).get();
		
		RepositoryBean toBeSavedOrganization = new RepositoryBean();
		toBeSavedOrganization.setOrganization(existingRepository.getOrganization());
		toBeSavedOrganization.setName(existingRepository.getName());
		toBeSavedOrganization.setDisplayName(existingRepository.getDisplayName());
		toBeSavedOrganization.setIsPublic(true);
		
		repositoryRepository.saveAndFlush(toBeSavedOrganization);
	}
	
	@Test
	@DbUnitMinimalDataSetup
	@DatabaseTearDown
	public void testDifferentOrganizationsCanHaveRepoWithTheSameName() throws IOException, GitAPIException, RepositoryNameNotUniqueException {
		
		OrganizationBean organization = new OrganizationBean();
		organization.setName("new_organization");
		organization.setDisplayName("New Organization");
		
		organizationRepository.saveAndFlush(organization);
		
		RepositoryBean existingRepository = repositoryRepository.findById(1).get();
		
		RepositoryBean toBeSavedRepository = new RepositoryBean();
		toBeSavedRepository.setOrganization(organization);
		toBeSavedRepository.setName(existingRepository.getName());
		toBeSavedRepository.setDisplayName(existingRepository.getDisplayName());
		toBeSavedRepository.setIsPublic(true);
		
		repositoryRepository.saveAndFlush(toBeSavedRepository);
	}
}
