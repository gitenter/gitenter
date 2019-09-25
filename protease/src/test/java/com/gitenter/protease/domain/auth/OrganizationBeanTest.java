package com.gitenter.protease.domain.auth;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;

import org.eclipse.jgit.api.errors.GitAPIException;
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
@DbUnitConfiguration(databaseConnection={
		"schemaAuthDatabaseConnection", 
		"schemaGitDatabaseConnection",
		"schemaTraceabilityDatabaseConnection",
		"schemaReviewDatabaseConnection"})
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
		
		/*
		 * This is to test there's no circular dependency which makes 
		 * toString() to stack overflow.
		 */
		assertNotNull(item.toString());
		
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
	public void testAddNewRepository() throws IOException, GitAPIException {
		
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
		repositoryRepository.init(repository);
		
		MemberBean member = memberRepository.findById(1).get();
		assertEquals(member.getRepositories(RepositoryMemberRole.ORGANIZER).size(), 1);
		
		RepositoryMemberMapBean map = RepositoryMemberMapBean.link(repository, member, RepositoryMemberRole.ORGANIZER);
		repositoryMemberMapRepository.saveAndFlush(map);
		
		MemberBean updatedMember = memberRepository.findById(1).get();
		assertEquals(updatedMember.getRepositories(RepositoryMemberRole.ORGANIZER).size(), 2);
		for (RepositoryBean iterRepository : updatedMember.getRepositories(RepositoryMemberRole.ORGANIZER)) {
			switch(iterRepository.getName()) {
			case "repository":
				break;
			case "new_repository":
				assertEquals(iterRepository.getDisplayName(), "New Repository");
				break;
			default:
				throw new IOException();
			}	
		}
		
		RepositoryBean updatedNewRepository = repositoryRepository.findByOrganizationNameAndRepositoryName(organization.getName(), "new_repository").get(0);
		assertEquals(updatedNewRepository.getMembers(RepositoryMemberRole.ORGANIZER).size(), 1);
		assertEquals(updatedNewRepository.getMembers(RepositoryMemberRole.ORGANIZER).get(0).getUsername(), member.getUsername());
		
		/*
		 * Delete is important, because git/folder change cannot be automatic roll back
		 * after test.
		 */
		repositoryRepository.delete(repository);
	}
}
