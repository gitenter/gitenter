package com.gitenter.domain.auth;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
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

import com.gitenter.annotation.DbUnitMinimalDataSetup;
import com.gitenter.dao.auth.RepositoryRepository;
import com.gitenter.domain.git.BranchBean;
import com.gitenter.domain.git.CommitBean;
import com.gitenter.domain.git.TagBean;
import com.gitenter.protease.ProteaseConfig;
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
@DbUnitConfiguration(databaseConnection={"schemaAuthDatabaseConnection", "schemaGitDatabaseConnection", "schemaReviewDatabaseConnection"})
public class RepositoryBeanTest {

	@Autowired RepositoryRepository repository;
	
	@Test
	@Transactional
	@DbUnitMinimalDataSetup
	public void testMinimalRepositoryInfomation() throws IOException, GitAPIException {
		
		RepositoryBean item = repository.findById(1).get();
		
		assertEquals(item.getName(), "repository");
		assertEquals(item.getDisplayName(), "Repository");
		assertEquals(item.getDescription(), "Repo description");
		assertEquals(item.getIsPublic(), true);

		assertEquals(item.getMembers(RepositoryMemberRole.REVIEWER).size(), 1);
		assertEquals(item.getMembers(RepositoryMemberRole.BLACKLIST).size(), 0);
	}
	
	@Test
	@Transactional
	@DatabaseSetup(connection="schemaAuthDatabaseConnection", value="classpath:dbunit/minimal/auth.xml")
	@DatabaseSetup(connection="schemaGitDatabaseConnection", value="classpath:dbunit/minimal/git.xml")
	public void testMinimalRepositoryGetBranches() throws IOException, GitAPIException {
		
		RepositoryBean item = repository.findById(1).get();
		
		Collection<BranchBean> branches = item.getBranches();
		assertEquals(branches.size(), 1);
		BranchBean branch = branches.iterator().next();
		assertEquals(branch.getName(), "master");
		
		CommitBean head = branch.getHead();
		assertEquals(head.getMessage(), "commit\n");
		
		List<CommitBean> log = branch.getLog(5, 0);
		assertEquals(log.size(), 1);
		assertEquals(log.get(0).getMessage(), "commit\n");
	}
	
	@Test
	@Transactional
	@DatabaseSetup(connection="schemaAuthDatabaseConnection", value="classpath:dbunit/minimal/auth.xml")
	@DatabaseSetup(connection="schemaGitDatabaseConnection", value="classpath:dbunit/minimal/git.xml")
	public void testMinimalRepositoryGetBranch() throws IOException, GitAPIException {
		
		RepositoryBean item = repository.findById(1).get();
		
		BranchBean branch = item.getBranch("master");
		CommitBean head = branch.getHead();
		assertEquals(head.getMessage(), "commit\n");
		
		List<CommitBean> log = branch.getLog(5, 0);
		assertEquals(log.size(), 1);
		assertEquals(log.get(0).getMessage(), "commit\n");
	}
	
	@Test
	@Transactional
	@DatabaseSetup(connection="schemaAuthDatabaseConnection", value="classpath:dbunit/minimal/auth.xml")
	@DatabaseSetup(connection="schemaGitDatabaseConnection", value="classpath:dbunit/minimal/git.xml")
	public void testMinimalRepositoryGetTags() throws IOException, GitAPIException {
		
		RepositoryBean item = repository.findById(1).get();
		
		Collection<TagBean> tags = item.getTags();
		assertEquals(tags.size(), 2);
		
		List<String> tagNames = new ArrayList<String>();
		Iterator<TagBean> tagIterator = tags.iterator();
		tagNames.add(tagIterator.next().getName());
		tagNames.add(tagIterator.next().getName());
		
		List<String> expectedTagNames = new ArrayList<String>();
		expectedTagNames.add("tag");
		expectedTagNames.add("annotated-tag");
		
		assertTrue(tagNames.containsAll(expectedTagNames));
		assertTrue(expectedTagNames.containsAll(tagNames));
	}
	
	@Test
	@Transactional
	@DatabaseSetup(connection="schemaAuthDatabaseConnection", value="classpath:dbunit/minimal/auth.xml")
	@DatabaseSetup(connection="schemaGitDatabaseConnection", value="classpath:dbunit/minimal/git.xml")
	public void testMinimalRepositoryGetTag() throws IOException, GitAPIException {
		
		RepositoryBean item = repository.findById(1).get();
		
		TagBean tag = item.getTag("tag");
		CommitBean commit = tag.getCommit();
		assertEquals(commit.getMessage(), "commit\n");
	}
}
