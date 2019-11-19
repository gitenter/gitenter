package com.gitenter.protease.domain.auth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;

import com.gitenter.protease.ProteaseConfig;
import com.gitenter.protease.annotation.DbUnitMinimalDataSetup;
import com.gitenter.protease.annotation.DbUnitMinimalDataTearDown;
import com.gitenter.protease.dao.auth.OrganizationRepository;
import com.gitenter.protease.dao.auth.PersonRepository;
import com.gitenter.protease.dao.auth.RepositoryPersonMapRepository;
import com.gitenter.protease.dao.auth.RepositoryRepository;
import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DbUnitConfiguration;

@ExtendWith(SpringExtension.class)
@ActiveProfiles(profiles = "minimal")
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
public class OrganizationBeanTest {

	@Autowired private OrganizationRepository organizationRepository;
	
	@Autowired PersonRepository personRepository;
	@Autowired RepositoryRepository repositoryRepository;
	@Autowired RepositoryPersonMapRepository repositoryPersonMapRepository;
	
	@Test
	@Transactional
	@DbUnitMinimalDataSetup
	public void testDbUnitMinimalQueryWorks() throws Exception {
		
		OrganizationBean item = organizationRepository.findById(1).get();
		
		/*
		 * This is to test there's no circular dependency which makes 
		 * toString() to stack overflow.
		 */
		assertNotNull(item.toString());
		
		assertEquals(item.getName(), "organization");
		assertEquals(item.getDisplayName(), "Organization");
		
		assertEquals(item.getPersons(OrganizationPersonRole.MANAGER).size(), 1);
		assertEquals(item.getPersons(OrganizationPersonRole.MEMBER).size(), 0);
	}
	
	/*
	 * TODO:
	 * 
	 * Rewrite this test under "organizationService".
	 */
	@Test
	@Transactional
	@DbUnitMinimalDataSetup
	@DbUnitMinimalDataTearDown
	public void testAddNewRepository() throws IOException {
		
		RepositoryBean repository = new RepositoryBean();
		repository.setName("new_repository");
		repository.setDisplayName("New Repository");
		repository.setIsPublic(true);
		
		OrganizationBean organization = organizationRepository.findById(1).get();
		assertEquals(organization.getRepositories().size(), 1);
		organization.addRepository(repository);
		assertEquals(organization.getRepositories().size(), 2);
		
		/*
		 * Need to refresh the ID of "repository", so will not
		 * work if saving by "organizationRepository".
		 */
		repositoryRepository.saveAndFlush(repository);
		
		PersonBean person = personRepository.findById(1).get();
		assertEquals(person.getRepositories(RepositoryPersonRole.ORGANIZER).size(), 1);
		
		RepositoryPersonMapBean map = RepositoryPersonMapBean.link(repository, person, RepositoryPersonRole.ORGANIZER);
		repositoryPersonMapRepository.saveAndFlush(map);
		
		PersonBean updatedPerson = personRepository.findById(1).get();
		assertEquals(updatedPerson.getRepositories(RepositoryPersonRole.ORGANIZER).size(), 2);
		for (RepositoryBean iterRepository : updatedPerson.getRepositories(RepositoryPersonRole.ORGANIZER)) {
			switch(iterRepository.getName()) {
			case "repository":
				break;
			case "new_repository":
				assertEquals(iterRepository.getDisplayName(), "New Repository");
				break;
			default:
				throw new IOException();
			}	
		}
		
		RepositoryBean updatedNewRepository = repositoryRepository.findByOrganizationNameAndRepositoryName(organization.getName(), "new_repository").get(0);
		assertEquals(updatedNewRepository.getPersons(RepositoryPersonRole.ORGANIZER).size(), 1);
		assertEquals(updatedNewRepository.getPersons(RepositoryPersonRole.ORGANIZER).get(0).getUsername(), person.getUsername());
	}
}
