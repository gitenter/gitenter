package com.gitenter.protease.domain.auth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.security.GeneralSecurityException;

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
import com.gitenter.protease.dao.auth.MemberRepository;
import com.gitenter.protease.dao.auth.OrganizationMemberMapRepository;
import com.gitenter.protease.dao.auth.OrganizationRepository;
import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseTearDown;
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
public class MemberBeanTest {

	@Autowired MemberRepository memberRepository;
	
	@Autowired OrganizationRepository organizationRepository;
	@Autowired OrganizationMemberMapRepository organizationMemberMapRepository;
	
	@Test
	@Transactional
	@DbUnitMinimalDataSetup
	public void testDbUnitMinimalQueryWorks() throws IOException, GeneralSecurityException {
		
		MemberBean item = memberRepository.findById(1).get();
		
		/*
		 * This is to test there's no circular dependency which makes 
		 * toString() to stack overflow.
		 */
		assertNotNull(item.toString());
		
		assertEquals(item.getUsername(), "username");
		assertEquals(item.getPassword(), "password");
		assertEquals(item.getDisplayName(), "Display Name");
		assertEquals(item.getEmail(), "email@email.com");
		assertTrue(item.getRegisterAt() != null);
		
		assertEquals(item.getSshKeys().size(), 1);
		SshKeyBean sshKey = item.getSshKeys().get(0);
		assertEquals(sshKey.getKeyType(), "ssh-rsa");
		assertEquals(sshKey.getKeyData(), "VGhpcyBpcyBteSB0ZXh0Lg==");
		assertEquals(sshKey.getComment(), "comment");
		
		assertEquals(item.getOrganizations(OrganizationMemberRole.MANAGER).size(), 1);
		assertEquals(item.getOrganizations(OrganizationMemberRole.MEMBER).size(), 0);

		assertEquals(item.getRepositories(RepositoryMemberRole.ORGANIZER).size(), 1);
		assertEquals(item.getRepositories(RepositoryMemberRole.EDITOR).size(), 0);
		assertEquals(item.getRepositories(RepositoryMemberRole.BLACKLIST).size(), 0);
	}
	
	/*
	 * TODO:
	 * 
	 * Rewrite this test under "memberService".
	 */
	@Test
	@Transactional
	@DbUnitMinimalDataSetup
	@DatabaseTearDown
	public void testAddNewOrganization() {
		
		MemberBean member = memberRepository.findById(1).get();
		assertEquals(member.getOrganizations(OrganizationMemberRole.MANAGER).size(), 1);

		OrganizationBean organization = new OrganizationBean();
		organization.setName("new_organization");
		organization.setDisplayName("New Organization");
		assertEquals(organization.getMembers(OrganizationMemberRole.MANAGER).size(), 0);
		
		/*
		 * Need to save first. Otherwise when saving 
		 * "OrganizationMemberMapBean", non-null error will
		 * be raised for "organization_id" column.
		 */
		organizationRepository.saveAndFlush(organization);
		
		OrganizationMemberMapBean map = OrganizationMemberMapBean.link(organization, member, OrganizationMemberRole.MANAGER);
		assertEquals(member.getOrganizations(OrganizationMemberRole.MANAGER).size(), 2);
		assertEquals(organization.getMembers(OrganizationMemberRole.MANAGER).size(), 1);
		
		/*
		 * Cannot using "memberRepository" or "organizationRepository"
		 * to save. It will double-insert the target row and cause primary
		 * key error.
		 */
		organizationMemberMapRepository.saveAndFlush(map);
		
		member = memberRepository.findById(1).get();
		assertEquals(member.getOrganizations(OrganizationMemberRole.MANAGER).size(), 2);
	}
}
