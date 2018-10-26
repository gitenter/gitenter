package com.gitenter.protease.domain.auth;

import static org.junit.Assert.assertEquals;

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
import com.gitenter.protease.dao.auth.OrganizationRepository;
import com.gitenter.protease.dao.auth.RepositoryMemberMapRepository;
import com.gitenter.protease.dao.auth.RepositoryRepository;
import com.gitenter.protease.domain.auth.OrganizationBean;
import com.gitenter.protease.domain.auth.OrganizationMemberRole;
import com.gitenter.protease.exception.RepositoryNameNotUniqueException;
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
public class OrganizationBeanTest {

	@Autowired private OrganizationRepository organizationRepository;
	
	@Autowired MemberRepository memberRepository;
	@Autowired RepositoryRepository repositoryRepository;
	@Autowired RepositoryMemberMapRepository repositoryMemberMapRepository;
	
	@Test
	@Transactional
	@DbUnitMinimalDataSetup
	public void testDbUnitMinimalQueryWorks() throws Exception {
		
		OrganizationBean item = organizationRepository.findById(1).get();
		
		assertEquals(item.getName(), "organization");
		assertEquals(item.getDisplayName(), "Organization");
		
		assertEquals(item.getMembers(OrganizationMemberRole.MANAGER).size(), 1);
		assertEquals(item.getMembers(OrganizationMemberRole.MEMBER).size(), 0);
	}
	
	/*
	 * TODO:
	 * 
	 * Rewrite this test under "organizationService".
	 */
	@Test
	@Transactional
	@DbUnitMinimalDataSetup
	@DatabaseTearDown
	public void testAddNewRepository() throws RepositoryNameNotUniqueException {
		
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
		
		MemberBean member = memberRepository.findById(1).get();
		assertEquals(member.getRepositories(RepositoryMemberRole.ORGANIZER).size(), 0);
		
		RepositoryMemberMapBean map = RepositoryMemberMapBean.link(repository, member, RepositoryMemberRole.ORGANIZER);
		repositoryMemberMapRepository.saveAndFlush(map);
		
		MemberBean updatedMember = memberRepository.findById(1).get();
		assertEquals(updatedMember.getRepositories(RepositoryMemberRole.ORGANIZER).size(), 1);
	}
}
