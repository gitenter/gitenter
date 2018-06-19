package com.gitenter.domain.auth;

import static org.junit.Assert.*;

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

import com.gitenter.dao.auth.MemberRepository;
import com.gitenter.domain.auth.MemberBean;
import com.gitenter.domain.auth.OrganizationMemberRole;
import com.gitenter.domain.auth.RepositoryMemberRole;
import com.gitenter.protease.ProteaseConfig;
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
public class MemberBeanTest {

	@Autowired MemberRepository repository;
	
	@Test
	@Transactional
	@DatabaseSetup(connection="schemaAuthDatabaseConnection", value="classpath:dbunit/minimal-schema-auth.xml")
	//@DatabaseTearDown("member.xml")
	public void testDbUnitMinimalQueryWorks() throws IOException {
		
		MemberBean item = repository.findById(1).get();
		
		assertEquals(item.getUsername(), "username");
		assertEquals(item.getPassword(), "password");
		assertEquals(item.getDisplayName(), "Display Name");
		assertEquals(item.getEmail(), "email@email.com");
		assertTrue(item.getRegistrationTimestamp() != null);
		
		assertEquals(item.getOrganizations(OrganizationMemberRole.MANAGER).size(), 1);
		assertEquals(item.getOrganizations(OrganizationMemberRole.MEMBER).size(), 0);

		assertEquals(item.getRepositories(RepositoryMemberRole.REVIEWER).size(), 1);
		assertEquals(item.getRepositories(RepositoryMemberRole.BLACKLIST).size(), 0);
	}
}
