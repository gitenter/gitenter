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
import com.gitenter.protease.domain.auth.OrganizationBean;
import com.gitenter.protease.domain.auth.OrganizationMemberMapBean;
import com.gitenter.protease.domain.auth.OrganizationMemberRole;
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
public class OrganizationMemberMapRepositoryTest {
	
	@Autowired OrganizationMemberMapRepository repository;
	
	@Autowired MemberRepository memberRepository;
	@Autowired OrganizationRepository organizationRepository;

	@Test
	@DbUnitMinimalDataSetup
	@DatabaseTearDown
	public void testFindByUsernameAndOrganizationId() {
		
		List<OrganizationMemberMapBean> managerMaps = repository.findByUsernameAndOrganizationId(
				"username", 1);
		
		assertEquals(managerMaps.size(), 1);
		assertEquals(managerMaps.get(0).getMember().getUsername(), "username");
	}
	
	@Test
	@DbUnitMinimalDataSetup
	@DatabaseTearDown
	public void testFindByUsernameAndOrganizationIdAndRole() {
		
		List<OrganizationMemberMapBean> managerMaps = repository.findByUsernameAndOrganizationIdAndRole(
				"username", 1, OrganizationMemberRole.MANAGER);
		
		assertEquals(managerMaps.size(), 1);
		assertEquals(managerMaps.get(0).getMember().getUsername(), "username");
	}
	
	@Test
	@DbUnitMinimalDataSetup
	@DatabaseTearDown
	public void testFindByMemberAndOrganization() {
		
		MemberBean member = memberRepository.findById(1).get();
		OrganizationBean organization = organizationRepository.findById(1).get();
		
		List<OrganizationMemberMapBean> allMaps = repository.fineByMemberAndOrganization(
				member, organization);
		
		assertEquals(allMaps.size(), 1);
		assertEquals(allMaps.get(0).getMember().getId(), new Integer(1));
	}
	
	@Test
	@DbUnitMinimalDataSetup
	@DatabaseTearDown
	public void testFindByMemberAndOrganizationAndRole() {
		
		MemberBean member = memberRepository.findById(1).get();
		OrganizationBean organization = organizationRepository.findById(1).get();
		
		List<OrganizationMemberMapBean> managerMaps = repository.fineByMemberAndOrganizationAndRole(
				member, organization, OrganizationMemberRole.MANAGER);
		
		assertEquals(managerMaps.size(), 1);
		assertEquals(managerMaps.get(0).getMember().getId(), new Integer(1));
		
		List<OrganizationMemberMapBean> memberMaps = repository.fineByMemberAndOrganizationAndRole(
				member, organization, OrganizationMemberRole.MEMBER);
		
		assertEquals(memberMaps.size(), 0);
	}
	
	@Test
	@Transactional
	@DbUnitMinimalDataSetup
	@DatabaseTearDown
	public void testRemoveUserFromOrganization() {
		
		MemberBean member = memberRepository.findById(1).get();
		assertEquals(member.getOrganizations(OrganizationMemberRole.MANAGER).size(), 1);
		
		Integer mapId = member.getOrganizationMemberMaps().get(0).getId();
		repository.throughSqlDeleteById(mapId);
		
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
