package com.gitenter.domain.auth;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

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
import org.springframework.transaction.annotation.Transactional;

import com.gitenter.dao.auth.RepositoryRepository;
import com.gitenter.domain.auth.RepositoryBean;
import com.gitenter.domain.auth.RepositoryMemberRole;
import com.gitenter.protease.*;
import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DbUnitConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles(profiles = "production")
@ContextConfiguration(classes=ProteaseConfig.class)
@TestExecutionListeners({
	DependencyInjectionTestExecutionListener.class,
	DirtiesContextTestExecutionListener.class,
	TransactionalTestExecutionListener.class,
	DbUnitTestExecutionListener.class })
@DbUnitConfiguration(databaseConnection={"schemaAuthDatabaseConnection"})
public class RepositoryBeanTest {

	@Autowired RepositoryRepository repository;
	
	@Test
	@Transactional
	@DatabaseSetup(connection="schemaAuthDatabaseConnection", value="classpath:dbunit/minimal-schema-auth.xml")
	public void testDbUnitMinimalQueryWorks() throws IOException {
		
		RepositoryBean item = repository.findById(1);
		
		assertEquals(item.getName(), "repository");
		assertEquals(item.getDisplayName(), "Repository");
		assertEquals(item.getDescription(), "Repo description");
		assertEquals(item.getIsPublic(), true);

		assertEquals(item.getMembers(RepositoryMemberRole.REVIEWER).size(), 1);
		assertEquals(item.getMembers(RepositoryMemberRole.BLACKLIST).size(), 0);
	}
}
