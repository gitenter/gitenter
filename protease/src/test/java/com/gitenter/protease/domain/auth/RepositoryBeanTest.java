package com.gitenter.protease.domain.auth;

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

import com.gitenter.protease.ProteaseConfig;
import com.gitenter.protease.annotation.DbUnitMinimalDataSetup;
import com.gitenter.protease.dao.auth.RepositoryRepository;
import com.gitenter.protease.dao.git.CommitRepository;
import com.gitenter.protease.domain.git.BranchBean;
import com.gitenter.protease.domain.git.CommitBean;
import com.gitenter.protease.domain.git.TagBean;
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
public class RepositoryBeanTest {

	@Autowired RepositoryRepository repository;
	@Autowired CommitRepository commitRepository;
	
	@Test
	@Transactional
	@DbUnitMinimalDataSetup
	public void testMinimalRepositoryInfomation() throws IOException, GitAPIException {
		
		RepositoryBean item = repository.findById(1).get();
		
		assertEquals(item.getName(), "repository");
		assertEquals(item.getDisplayName(), "Repository");
		assertEquals(item.getDescription(), "Repo description");
		assertEquals(item.getIsPublic(), true);

		assertEquals(item.getMembers(RepositoryMemberRole.ORGANIZER).size(), 0);
		assertEquals(item.getMembers(RepositoryMemberRole.EDITOR).size(), 1);
		assertEquals(item.getMembers(RepositoryMemberRole.BLACKLIST).size(), 0);
	}
	
	@Test
	@Transactional
	@DbUnitMinimalDataSetup
	public void testMinimalRepositoryGetCommitCount() throws IOException, GitAPIException {
		
		RepositoryBean item = repository.findById(1).get();
		
		/*
		 * This is actually to test that Hibernate will indeed fire 
		 * > select count(id) from git.git_commit where repository_id =?
		 * However we can only print out the SQL and check it using our eye bowl.
		 */
		assertEquals(item.getCommitCount(), 1);
	}
	
	@Test
	@Transactional
	@DbUnitMinimalDataSetup
	public void testMinimalRepositoryGetBranches() throws IOException, GitAPIException {
		
		RepositoryBean item = repository.findById(1).get();
		
		Collection<BranchBean> branches = item.getBranches();
		assertEquals(branches.size(), 1);
		BranchBean branch = branches.iterator().next();
		assertEquals(branch.getName(), "master");
		
		CommitBean head = branch.getHead();
		assertEquals(head.getMessage(), "commit\n");
	}
	
	@Test
	@Transactional
	@DbUnitMinimalDataSetup
	public void testMinimalRepositoryGetBranch() throws IOException, GitAPIException {
		
		RepositoryBean item = repository.findById(1).get();
		
		BranchBean branch = item.getBranch("master");
		CommitBean head = branch.getHead();
		assertEquals(head.getMessage(), "commit\n");
	}
	
	@Test
	@Transactional
	@DbUnitMinimalDataSetup
	public void testMinimalRepositoryGetInDatabaseLogByMaxCountAndSkip() throws IOException, GitAPIException {
		
		RepositoryBean item = repository.findById(1).get();
		BranchBean branch = item.getBranch("master");
		
		List<CommitBean> log = branch.getInDatabaseLog(5, 0);
		assertEquals(log.size(), 1);
		assertEquals(log.get(0).getMessage(), "commit\n");
		
		log = branch.getInDatabaseLog(5, 1);
		assertEquals(log.size(), 0);
	}
	
	@Test
	@Transactional
	@DbUnitMinimalDataSetup
	public void testMinimalRepositoryGetInDatabaseLogByOldAndNewSha() throws IOException, GitAPIException {
		
		CommitBean commit = commitRepository.findById(1).get();
		String newSha = commit.getSha();
		String oldSha = "0000000000000000000000000000000000000000";
		
		RepositoryBean item = repository.findById(1).get();
		BranchBean branch = item.getBranch("master");
		
		List<CommitBean> log = branch.getInDatabaseLog(oldSha, newSha);
		assertEquals(log.size(), 1);
		assertEquals(log.get(0).getMessage(), "commit\n");
	}
	
	@Test
	@Transactional
	@DbUnitMinimalDataSetup
	public void testMinimalRepositoryGetUnsavedLogByMaxCountAndSkip() throws IOException, GitAPIException {
		
		commitRepository.deleteById(1);
		
		RepositoryBean item = repository.findById(1).get();
		BranchBean branch = item.getBranch("master");
		
		List<CommitBean> log = branch.getInDatabaseLog(5, 0);
		assertEquals(log.size(), 0);
		
		List<CommitBean.GitCommitDatapack> unsavedLog = branch.getUnsavedLog(5, 0);
		assertEquals(unsavedLog.size(), 1);
		assertEquals(unsavedLog.get(0).getMessage(), "commit\n");
	}
	
	@Test
	@Transactional
	@DbUnitMinimalDataSetup
	public void testMinimalRepositoryGetUnsavedLogByOldAndNewSha() throws IOException, GitAPIException {
		
		CommitBean commit = commitRepository.findById(1).get();
		String newSha = commit.getSha();
		String oldSha = "0000000000000000000000000000000000000000";
		
		commitRepository.deleteById(1);
		
		RepositoryBean item = repository.findById(1).get();
		BranchBean branch = item.getBranch("master");
		
		List<CommitBean> log = branch.getInDatabaseLog(oldSha, newSha);
		assertEquals(log.size(), 0);
		
		List<CommitBean.GitCommitDatapack> unsavedLog = branch.getUnsavedLog(oldSha, newSha);
		assertEquals(unsavedLog.size(), 1);
		assertEquals(unsavedLog.get(0).getMessage(), "commit\n");
	}
	
	@Test
	@Transactional
	@DbUnitMinimalDataSetup
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
	@DbUnitMinimalDataSetup
	public void testMinimalRepositoryGetTag() throws IOException, GitAPIException {
		
		RepositoryBean item = repository.findById(1).get();
		
		TagBean tag = item.getTag("tag");
		CommitBean commit = tag.getCommit();
		assertEquals(commit.getMessage(), "commit\n");
	}
}
