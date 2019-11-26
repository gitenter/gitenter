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
import com.gitenter.protease.domain.auth.UserBean;
import com.gitenter.protease.domain.auth.RepositoryBean;
import com.gitenter.protease.domain.auth.RepositoryUserMapBean;
import com.gitenter.protease.domain.auth.RepositoryUserRole;
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
public class RepositoryUserMapRepositoryTest {
	
	@Autowired RepositoryUserMapRepository repositoryUserMapRepository;
	
	@Autowired UserRepository userRepository;
	@Autowired RepositoryRepository repositoryRepository;
	
	@Test
	@DbUnitMinimalDataSetup
	@DbUnitMinimalDataTearDown
	public void testFindByUsernameAndRepositoryId() {
		
		List<RepositoryUserMapBean> maps = repositoryUserMapRepository.findByUsernameAndRepositoryId("username", 1);		
		
		assertEquals(maps.size(), 1);
		assertEquals(maps.get(0).getUser().getUsername(), "username");
	}
	
	@Test
	@DbUnitMinimalDataSetup
	@DbUnitMinimalDataTearDown
	public void testFindByUsernameAndOrganizationNameAndRepositoryName() {
		
		List<RepositoryUserMapBean> maps = repositoryUserMapRepository.findByUsernameAndOrganizationNameAndRepositoryName(
				"username", "organization", "repository");		
		
		assertEquals(maps.size(), 1);
		assertEquals(maps.get(0).getUser().getUsername(), "username");
	}
	
	@Test
	@Transactional
	@DbUnitMinimalDataSetup
	@DbUnitMinimalDataTearDown
	public void testRemoveUserFromRepsoitory() {
		
		UserBean user = userRepository.findById(1).get();
		assertEquals(user.getRepositories(RepositoryUserRole.PROJECT_ORGANIZER).size(), 1);
		
		Integer mapId = user.getRepositoryUserMaps().get(0).getId();
		repositoryUserMapRepository.throughSqlDeleteById(mapId);
		
		/*
		 * Can't do it. Because Hibernate will be too smart to not generate the query
		 * to touch the database again (identity mapping pattern), so the assert will
		 * be wrong.
		 */
//		user = userRepository.findById(1).get();
//		assertEquals(user.getRepositories(RepositoryUserRole.PROJECGT_ORGANIZER).size(), 0);
		
		RepositoryBean repository = repositoryRepository.findById(1).get();
		assertEquals(repository.getUsers(RepositoryUserRole.PROJECT_ORGANIZER).size(), 0);
	}
}
