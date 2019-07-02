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
import org.springframework.transaction.annotation.Transactional;

import com.gitenter.protease.ProteaseConfig;
import com.gitenter.protease.annotation.DbUnitMinimalDataSetup;
import com.gitenter.protease.domain.auth.MemberBean;
import com.gitenter.protease.domain.auth.RepositoryBean;
import com.gitenter.protease.domain.auth.RepositoryMemberMapBean;
import com.gitenter.protease.domain.auth.RepositoryMemberRole;
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
	
	@Autowired RepositoryMemberMapRepository repositoryMemberMapRepository;
	
	@Autowired MemberRepository memberRepository;
	@Autowired RepositoryRepository repositoryRepository;
	
	@Test
	@DbUnitMinimalDataSetup
	@DatabaseTearDown
	public void testFindByUsernameAndRepositoryId() {
		
		List<RepositoryMemberMapBean> maps = repositoryMemberMapRepository.findByUsernameAndRepositoryId("username", 1);		
		
		assertEquals(maps.size(), 1);
		assertEquals(maps.get(0).getMember().getUsername(), "username");
	}
	
	@Test
	@DbUnitMinimalDataSetup
	@DatabaseTearDown
	public void testFindByUsernameAndOrganizationNameAndRepositoryName() {
		
		List<RepositoryMemberMapBean> maps = repositoryMemberMapRepository.findByUsernameAndOrganizationNameAndRepositoryName(
				"username", "organization", "repository");		
		
		assertEquals(maps.size(), 1);
		assertEquals(maps.get(0).getMember().getUsername(), "username");
	}
	
	@Test
	@Transactional
	@DbUnitMinimalDataSetup
	@DatabaseTearDown
	public void testRemoveUserFromRepsoitory() {
		
		MemberBean member = memberRepository.findById(1).get();
		assertEquals(member.getRepositories(RepositoryMemberRole.ORGANIZER).size(), 1);
		
		Integer mapId = member.getRepositoryMemberMaps().get(0).getId();
		repositoryMemberMapRepository.throughSqlDeleteById(mapId);
		
		/*
		 * Can't do it. Because Hibernate will be too smart to not generate the query
		 * to touch the database again (identity mapping pattern), so the assert will
		 * be wrong.
		 */
//		member = memberRepository.findById(1).get();
//		assertEquals(member.getRepositories(RepositoryMemberRole.ORGANIZER).size(), 0);
		
		RepositoryBean repository = repositoryRepository.findById(1).get();
		assertEquals(repository.getMembers(RepositoryMemberRole.ORGANIZER).size(), 0);
	}
}
