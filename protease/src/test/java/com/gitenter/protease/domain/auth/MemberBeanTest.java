package com.gitenter.protease.domain.auth;

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

import com.gitenter.protease.ProteaseConfig;
import com.gitenter.protease.annotation.DbUnitMinimalDataSetup;
import com.gitenter.protease.dao.auth.MemberRepository;
import com.gitenter.protease.dao.auth.OrganizationMemberMapRepository;
import com.gitenter.protease.dao.auth.OrganizationRepository;
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
public class MemberBeanTest {

	@Autowired MemberRepository memberRepository;
	
	@Autowired OrganizationRepository organizationRepository;
	@Autowired OrganizationMemberMapRepository organizationMemberMapRepository;
	
	@Test
	@Transactional
	@DbUnitMinimalDataSetup
	public void testDbUnitMinimalQueryWorks() throws IOException, GeneralSecurityException {
		
		MemberBean item = memberRepository.findById(1).get();
		
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

		assertEquals(item.getRepositories(RepositoryMemberRole.EDITOR).size(), 1);
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
	
	@Test
	@Transactional
	@DbUnitMinimalDataSetup
	@DatabaseTearDown
	public void testRemoveOrganization() {
		
		MemberBean member = memberRepository.findById(1).get();
		assertEquals(member.getOrganizations(OrganizationMemberRole.MANAGER).size(), 1);
		
		Integer mapId = member.getOrganizationMemberMaps().get(0).getId();
		organizationMemberMapRepository.throughSqldeleteById(mapId);
		
		/*
		 * Can't do it. Because Hibernate will be too smart to not generate the query
		 * to touch the database again (identity mapping pattern), so the assert will
		 * be wrong.
		 */
//		member = memberRepository.findById(1).get();
//		assertEquals(member.getOrganizations(OrganizationMemberRole.MANAGER).size(), 0);
		
		OrganizationBean organization = organizationRepository.findById(1).get();
		assertEquals(organization.getMembers(OrganizationMemberRole.MANAGER).size(), 0);
	}
}
