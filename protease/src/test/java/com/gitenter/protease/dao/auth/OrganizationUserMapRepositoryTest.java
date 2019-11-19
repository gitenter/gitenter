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
import com.gitenter.protease.domain.auth.OrganizationUserMapBean;
import com.gitenter.protease.domain.auth.OrganizationUserRole;
import com.gitenter.protease.domain.auth.UserBean;
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
public class OrganizationUserMapRepositoryTest {
	
	@Autowired OrganizationUserMapRepository repository;
	
	@Autowired UserRepository userRepository;
	@Autowired OrganizationRepository organizationRepository;

	@Test
	@DbUnitMinimalDataSetup
	@DbUnitMinimalDataTearDown
	public void testFindByUsernameAndOrganizationId() {
		
		List<OrganizationUserMapBean> managerMaps = repository.findByUsernameAndOrganizationId(
				"username", 1);
		
		assertEquals(managerMaps.size(), 1);
		assertEquals(managerMaps.get(0).getUser().getUsername(), "username");
	}
	
	@Test
	@DbUnitMinimalDataSetup
	@DbUnitMinimalDataTearDown
	public void testFindByUsernameAndOrganizationIdAndRole() {
		
		List<OrganizationUserMapBean> managerMaps = repository.findByUsernameAndOrganizationIdAndRole(
				"username", 1, OrganizationUserRole.MANAGER);
		
		assertEquals(managerMaps.size(), 1);
		assertEquals(managerMaps.get(0).getUser().getUsername(), "username");
	}
	
	@Test
	@DbUnitMinimalDataSetup
	@DbUnitMinimalDataTearDown
	public void testFindByUserAndOrganization() {
		
		UserBean user = userRepository.findById(1).get();
		OrganizationBean organization = organizationRepository.findById(1).get();
		
		List<OrganizationUserMapBean> allMaps = repository.fineByUserAndOrganization(
				user, organization);
		
		assertEquals(allMaps.size(), 1);
		assertEquals(allMaps.get(0).getUser().getId(), Integer.valueOf(1));
	}
	
	@Test
	@DbUnitMinimalDataSetup
	@DbUnitMinimalDataTearDown
	public void testFindByUserAndOrganizationAndRole() {
		
		UserBean user = userRepository.findById(1).get();
		OrganizationBean organization = organizationRepository.findById(1).get();
		
		List<OrganizationUserMapBean> managerMaps = repository.fineByUserAndOrganizationAndRole(
				user, organization, OrganizationUserRole.MANAGER);
		
		assertEquals(managerMaps.size(), 1);
		assertEquals(managerMaps.get(0).getUser().getId(), Integer.valueOf(1));
		
		List<OrganizationUserMapBean> userMaps = repository.fineByUserAndOrganizationAndRole(
				user, organization, OrganizationUserRole.MEMBER);
		
		assertEquals(userMaps.size(), 0);
	}
	
	@Test
	@Transactional
	@DbUnitMinimalDataSetup
	@DbUnitMinimalDataTearDown
	public void testRemoveUserFromOrganization() {
		
		UserBean user = userRepository.findById(1).get();
		assertEquals(user.getOrganizations(OrganizationUserRole.MANAGER).size(), 1);
		
		Integer mapId = user.getOrganizationUserMaps().get(0).getId();
		repository.throughSqlDeleteById(mapId);
		
		/*
		 * Can't do it. Because Hibernate will be too smart to not generate the query
		 * to touch the database again (identity mapping pattern), so the assert will
		 * be wrong.
		 */
//		user = userRepository.findById(1).get();
//		assertEquals(user.getOrganizations(OrganizationUserRole.MANAGER).size(), 0);
		
		OrganizationBean organization = organizationRepository.findById(1).get();
		assertEquals(organization.getUsers(OrganizationUserRole.MANAGER).size(), 0);
	}
}
