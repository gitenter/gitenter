package com.gitenter.protease.dao.auth;

import static org.junit.Assert.assertEquals;

import java.util.List;

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
import com.gitenter.protease.domain.auth.RepositoryMemberMapBean;
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
public class RepositoryMemberMapRepositoryTest {
	
	@Autowired RepositoryMemberMapRepository repository;
	
	@Test
	@DbUnitMinimalDataSetup
	@DatabaseTearDown
	public void testFindByUsernameAndRepositoryId() {
		
		List<RepositoryMemberMapBean> maps = repository.findByUsernameAndRepositoryId("username", 1);		
		
		assertEquals(maps.size(), 1);
		assertEquals(maps.get(0).getMember().getUsername(), "username");
	}
	
	@Test
	@DbUnitMinimalDataSetup
	@DatabaseTearDown
	public void testFindByUsernameAndOrganizationNameAndRepositoryName() {
		
		List<RepositoryMemberMapBean> maps = repository.findByUsernameAndOrganizationNameAndRepositoryName(
				"username", "organization", "repository");		
		
		assertEquals(maps.size(), 1);
		assertEquals(maps.get(0).getMember().getUsername(), "username");
	}
}
