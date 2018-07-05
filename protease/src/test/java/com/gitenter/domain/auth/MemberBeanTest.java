package com.gitenter.domain.auth;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.security.GeneralSecurityException;

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

import com.gitenter.annotation.DbUnitMinimalDataSetup;
import com.gitenter.dao.auth.MemberRepository;
import com.gitenter.protease.ProteaseConfig;
import com.github.springtestdbunit.DbUnitTestExecutionListener;
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
public class MemberBeanTest {

	@Autowired MemberRepository repository;
	
	@Test
	@Transactional
	@DbUnitMinimalDataSetup
	public void testDbUnitMinimalQueryWorks() throws IOException, GeneralSecurityException {
		
		MemberBean item = repository.findById(1).get();
		
		assertEquals(item.getUsername(), "username");
		assertEquals(item.getPassword(), "password");
		assertEquals(item.getDisplayName(), "Display Name");
		assertEquals(item.getEmail(), "email@email.com");
		assertTrue(item.getRegisterAt() != null);
		
		assertEquals(item.getSshKeys().size(), 1);
		SshKeyBean sshKey = item.getSshKeys().get(0);
		assertEquals(sshKey.getKeyType(), "ssh-rsa");
		assertEquals(sshKey.getKeyDataToString(), "VGhpcyBpcyBteSB0ZXh0Lg==");
		assertEquals(sshKey.getComment(), "comment");
		
		assertEquals(item.getOrganizations(OrganizationMemberRole.MANAGER).size(), 1);
		assertEquals(item.getOrganizations(OrganizationMemberRole.MEMBER).size(), 0);

		assertEquals(item.getRepositories(RepositoryMemberRole.REVIEWER).size(), 1);
		assertEquals(item.getRepositories(RepositoryMemberRole.BLACKLIST).size(), 0);
	}
}
