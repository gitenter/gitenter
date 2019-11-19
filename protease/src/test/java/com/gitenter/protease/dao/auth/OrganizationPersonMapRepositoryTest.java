package com.gitenter.protease.dao.auth;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

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
import com.gitenter.protease.domain.auth.OrganizationBean;
import com.gitenter.protease.domain.auth.OrganizationPersonMapBean;
import com.gitenter.protease.domain.auth.OrganizationPersonRole;
import com.gitenter.protease.domain.auth.PersonBean;
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
public class OrganizationPersonMapRepositoryTest {
	
	@Autowired OrganizationPersonMapRepository repository;
	
	@Autowired PersonRepository personRepository;
	@Autowired OrganizationRepository organizationRepository;

	@Test
	@DbUnitMinimalDataSetup
	@DbUnitMinimalDataTearDown
	public void testFindByUsernameAndOrganizationId() {
		
		List<OrganizationPersonMapBean> managerMaps = repository.findByUsernameAndOrganizationId(
				"username", 1);
		
		assertEquals(managerMaps.size(), 1);
		assertEquals(managerMaps.get(0).getPerson().getUsername(), "username");
	}
	
	@Test
	@DbUnitMinimalDataSetup
	@DbUnitMinimalDataTearDown
	public void testFindByUsernameAndOrganizationIdAndRole() {
		
		List<OrganizationPersonMapBean> managerMaps = repository.findByUsernameAndOrganizationIdAndRole(
				"username", 1, OrganizationPersonRole.MANAGER);
		
		assertEquals(managerMaps.size(), 1);
		assertEquals(managerMaps.get(0).getPerson().getUsername(), "username");
	}
	
	@Test
	@DbUnitMinimalDataSetup
	@DbUnitMinimalDataTearDown
	public void testFindByPersonAndOrganization() {
		
		PersonBean person = personRepository.findById(1).get();
		OrganizationBean organization = organizationRepository.findById(1).get();
		
		List<OrganizationPersonMapBean> allMaps = repository.fineByPersonAndOrganization(
				person, organization);
		
		assertEquals(allMaps.size(), 1);
		assertEquals(allMaps.get(0).getPerson().getId(), Integer.valueOf(1));
	}
	
	@Test
	@DbUnitMinimalDataSetup
	@DbUnitMinimalDataTearDown
	public void testFindByPersonAndOrganizationAndRole() {
		
		PersonBean person = personRepository.findById(1).get();
		OrganizationBean organization = organizationRepository.findById(1).get();
		
		List<OrganizationPersonMapBean> managerMaps = repository.fineByPersonAndOrganizationAndRole(
				person, organization, OrganizationPersonRole.MANAGER);
		
		assertEquals(managerMaps.size(), 1);
		assertEquals(managerMaps.get(0).getPerson().getId(), Integer.valueOf(1));
		
		List<OrganizationPersonMapBean> personMaps = repository.fineByPersonAndOrganizationAndRole(
				person, organization, OrganizationPersonRole.MEMBER);
		
		assertEquals(personMaps.size(), 0);
	}
	
	@Test
	@Transactional
	@DbUnitMinimalDataSetup
	@DbUnitMinimalDataTearDown
	public void testRemoveUserFromOrganization() {
		
		PersonBean person = personRepository.findById(1).get();
		assertEquals(person.getOrganizations(OrganizationPersonRole.MANAGER).size(), 1);
		
		Integer mapId = person.getOrganizationPersonMaps().get(0).getId();
		repository.throughSqlDeleteById(mapId);
		
		/*
		 * Can't do it. Because Hibernate will be too smart to not generate the query
		 * to touch the database again (identity mapping pattern), so the assert will
		 * be wrong.
		 */
//		person = personRepository.findById(1).get();
//		assertEquals(person.getOrganizations(OrganizationPersonRole.MANAGER).size(), 0);
		
		OrganizationBean organization = organizationRepository.findById(1).get();
		assertEquals(organization.getPersons(OrganizationPersonRole.MANAGER).size(), 0);
	}
}
