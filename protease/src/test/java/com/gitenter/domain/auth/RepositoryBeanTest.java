package com.gitenter.domain.auth;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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

import com.gitenter.dao.auth.RepositoryRepository;
import com.gitenter.domain.auth.RepositoryBean;
import com.gitenter.domain.auth.RepositoryMemberRole;
import com.gitenter.domain.git.BranchBean;
import com.gitenter.domain.git.CommitBean;
import com.gitenter.protease.*;
import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DbUnitConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles(profiles = "minimal")
@ContextConfiguration(classes=ProteaseConfig.class)
@TestExecutionListeners({
	DependencyInjectionTestExecutionListener.class,
	DirtiesContextTestExecutionListener.class,
	TransactionalTestExecutionListener.class,
	DbUnitTestExecutionListener.class })
@DbUnitConfiguration(databaseConnection={"schemaAuthDatabaseConnection", "schemaGitDatabaseConnection"})
public class RepositoryBeanTest {

	@Autowired RepositoryRepository repository;
	
	@Test
	@Transactional
	@DatabaseSetup(connection="schemaAuthDatabaseConnection", value="classpath:dbunit/minimal/auth.xml")
	@DatabaseSetup(connection="schemaGitDatabaseConnection", value="classpath:dbunit/minimal/git.xml")
	public void testDbUnitMinimalQueryWorks() throws IOException, GitAPIException {
		
		RepositoryBean item = repository.findById(1).get();
		
		assertEquals(item.getName(), "repository");
		assertEquals(item.getDisplayName(), "Repository");
		assertEquals(item.getDescription(), "Repo description");
		assertEquals(item.getIsPublic(), true);

		assertEquals(item.getMembers(RepositoryMemberRole.REVIEWER).size(), 1);
		assertEquals(item.getMembers(RepositoryMemberRole.BLACKLIST).size(), 0);
		
		Collection<BranchBean> branches = item.getBranches();
		assertEquals(branches.size(), 1);
		BranchBean branch = branches.iterator().next();
		assertEquals(branch.getName(), "master");
		
		CommitBean head = branch.getHead();
		assertEquals(head.getMessage(), "file\n");
		
		List<CommitBean> log = branch.getLog(5, 0);
		assertEquals(log.size(), 1);
		assertEquals(log.get(0).getMessage(), "file\n");
	}
}
