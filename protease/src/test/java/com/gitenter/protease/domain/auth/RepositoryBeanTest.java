package com.gitenter.protease.domain.auth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jgit.api.errors.GitAPIException;
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

import com.gitenter.gitar.GitCommit;
import com.gitenter.protease.ProteaseConfig;
import com.gitenter.protease.annotation.DbUnitMinimalDataSetup;
import com.gitenter.protease.dao.auth.PersonRepository;
import com.gitenter.protease.dao.auth.RepositoryPersonMapRepository;
import com.gitenter.protease.dao.auth.RepositoryRepository;
import com.gitenter.protease.dao.git.CommitRepository;
import com.gitenter.protease.domain.git.BranchBean;
import com.gitenter.protease.domain.git.CommitBean;
import com.gitenter.protease.domain.git.TagBean;
import com.github.springtestdbunit.DbUnitTestExecutionListener;
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
public class RepositoryBeanTest {

	@Autowired RepositoryRepository repository;
	
	@Autowired PersonRepository personRepository;
	@Autowired CommitRepository commitRepository;
	@Autowired RepositoryPersonMapRepository repositoryPersonMapRepository;
	
	@Test
	@Transactional
	@DbUnitMinimalDataSetup
	public void testMinimalRepositoryInfomation() throws IOException, GitAPIException {
		
		RepositoryBean item = repository.findById(1).get();
		
		/*
		 * This is to test there's no circular dependency which makes 
		 * toString() to stack overflow.
		 */
		assertNotNull(item.toString());
		
		assertEquals(item.getName(), "repository");
		assertEquals(item.getDisplayName(), "Repository");
		assertEquals(item.getDescription(), "Repo description");
		assertEquals(item.getIsPublic(), true);

		assertEquals(item.getPersons(RepositoryPersonRole.ORGANIZER).size(), 1);
		assertEquals(item.getPersons(RepositoryPersonRole.EDITOR).size(), 0);
		assertEquals(item.getPersons(RepositoryPersonRole.BLACKLIST).size(), 0);
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
		
		BranchBean masterBranch = item.getBranch("master");
		assertEquals(masterBranch.getName(), "master");
		
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
	public void testMinimalRepositoryGetShortNameBranch() throws IOException, GitAPIException {
		
		RepositoryBean item = repository.findById(1).get();
		
		BranchBean branch = item.getBranch("master");
		CommitBean head = branch.getHead();
		assertEquals(head.getMessage(), "commit\n");
	}
	
	@Test
	@Transactional
	@DbUnitMinimalDataSetup
	public void testMinimalRepositoryGetFullNameBranch() throws IOException, GitAPIException {
		
		RepositoryBean item = repository.findById(1).get();
		
		BranchBean branch = item.getBranch("refs/heads/master");
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
		String oldSha = GitCommit.EMPTY_SHA;
		
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
		
		List<GitCommit> unsavedLog = branch.getUnsavedLog(5, 0);
		assertEquals(unsavedLog.size(), 1);
		assertEquals(unsavedLog.get(0).getMessage(), "commit\n");
	}
	
	@Test
	@Transactional
	@DbUnitMinimalDataSetup
	public void testMinimalRepositoryGetUnsavedLogByOldAndNewSha() throws IOException, GitAPIException {
		
		CommitBean commit = commitRepository.findById(1).get();
		String newSha = commit.getSha();
		String oldSha = GitCommit.EMPTY_SHA;
		
		commitRepository.deleteById(1);
		
		RepositoryBean item = repository.findById(1).get();
		BranchBean branch = item.getBranch("master");
		
		List<CommitBean> log = branch.getInDatabaseLog(oldSha, newSha);
		assertEquals(log.size(), 0);
		
		List<GitCommit> unsavedLog = branch.getUnsavedLog(oldSha, newSha);
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
	
	@Test
	@Transactional
	@DbUnitMinimalDataSetup
	public void testAddCollaboator() throws IOException, GitAPIException {
		
		RepositoryBean item = repository.findById(1).get();
		assertEquals(item.getPersons(RepositoryPersonRole.ORGANIZER).size(), 1);
		assertEquals(item.getPersons(RepositoryPersonRole.EDITOR).size(), 0);
		assertEquals(item.getPersons(RepositoryPersonRole.BLACKLIST).size(), 0);
		
		PersonBean editor = new PersonBean();
		editor.setUsername("editor");
		editor.setPassword("password");
		editor.setDisplayName("Editor");
		editor.setEmail("editor@email.com");
		editor.setRegisterAt(new Date());
		
		personRepository.saveAndFlush(editor);
		
		RepositoryPersonMapBean map = RepositoryPersonMapBean.link(item, editor, RepositoryPersonRole.EDITOR);
		repositoryPersonMapRepository.saveAndFlush(map);
		
		RepositoryBean updatedItem = repository.findById(1).get();
		assertEquals(updatedItem.getPersons(RepositoryPersonRole.ORGANIZER).size(), 1);
		assertEquals(updatedItem.getPersons(RepositoryPersonRole.EDITOR).size(), 1);
		assertEquals(updatedItem.getPersons(RepositoryPersonRole.BLACKLIST).size(), 0);
		assertEquals(updatedItem.getPersons(RepositoryPersonRole.EDITOR).get(0).getUsername(), "editor");
		
		PersonBean updatedEditor = personRepository.findByUsername("editor").get(0);
		assertEquals(updatedEditor.getRepositories(RepositoryPersonRole.ORGANIZER).size(), 0);
		assertEquals(updatedEditor.getRepositories(RepositoryPersonRole.EDITOR).size(), 1);
		assertEquals(updatedEditor.getRepositories(RepositoryPersonRole.BLACKLIST).size(), 0);
		assertEquals(updatedEditor.getRepositories(RepositoryPersonRole.EDITOR).get(0).getName(), item.getName());
	}
}
