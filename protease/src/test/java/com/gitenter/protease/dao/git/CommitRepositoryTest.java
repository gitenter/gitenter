package com.gitenter.protease.dao.git;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Arrays;

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
import com.gitenter.protease.domain.auth.RepositoryBean;
import com.gitenter.protease.domain.git.CommitBean;
import com.gitenter.protease.domain.git.DocumentBean;
import com.gitenter.protease.domain.git.TraceableItemBean;
import com.gitenter.protease.domain.git.ValidCommitBean;
import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
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
public class CommitRepositoryTest {
	
	@Autowired CommitRepository commitRepository;
	
	@Autowired RepositoryRepository repositoryRepository;
	@Autowired DocumentRepository documentRepository;

	@Test
	@DbUnitMinimalDataSetup
	@DatabaseTearDown
	public void testDeleteById() throws IOException, GitAPIException {
		
		assertTrue(commitRepository.findById(1).isPresent());
		assertTrue(documentRepository.findById(1).isPresent());
		
		commitRepository.deleteById(1);
		
		/*
		 * Also confirmed that "ON DELETE CASCADE" is transfered to here.
		 */
		assertFalse(commitRepository.findById(1).isPresent());
		assertFalse(documentRepository.findById(1).isPresent());
	}
	
	@Test
	@Transactional
	@DatabaseSetup(connection="schemaAuthDatabaseConnection", value="classpath:dbunit/minimal/auth.xml")
	@DatabaseTearDown
	public void testSave() throws IOException, GitAPIException {
		
		assertFalse(commitRepository.findById(1).isPresent());
		
		RepositoryBean repository = repositoryRepository.findById(1).get();
		assertEquals(repository.getCommitCount(), 0);
		
		ValidCommitBean commit1 = new ValidCommitBean();
		commit1.setSha("fake-sha-1");
		commit1.setRepository(repository);
		repository.addCommit(commit1);
		
		commitRepository.saveAndFlush(commit1);
		
		repositoryRepository.findById(1).get();
		assertEquals(repository.getCommitCount(), 1);
		
		ValidCommitBean commit2 = new ValidCommitBean();
		commit2.setSha("fake-sha-2");
		commit2.setRepository(repository);
		repository.addCommit(commit2);
		
		commitRepository.saveAndFlush(commit2);
		
		repositoryRepository.findById(1).get();
		assertEquals(repository.getCommitCount(), 2);
	}
	
	/*
	 * This tests, as basically for testing Hibernate/Spring data `save()` method,
	 * is because we used to see weird behavior while saving complicated models.
	 * 
	 * That may be just a fakr alarm but we'll just keep those tests in here in
	 * case we need further investigations.
	 */
	@Test
	@Transactional
	@DatabaseSetup(connection="schemaAuthDatabaseConnection", value="classpath:dbunit/minimal/auth.xml")
	@DatabaseTearDown
	public void testSaveAndFlushWorksInMinimalDbUnitSetup() throws IOException, GitAPIException {
		
		assertFalse(commitRepository.findById(1).isPresent());
		
		RepositoryBean repository = repositoryRepository.findById(1).get();
		assertEquals(repository.getCommitCount(), 0);
		
		ValidCommitBean commit = new ValidCommitBean();
		commit.setSha("c36a5aed6e1c9f6a6c59bb21288a9d0bdbe93b73");
		commit.setRepository(repository);
		repository.addCommit(commit);
		
		DocumentBean document = new DocumentBean();
		document.setRelativePath("file");
		document.setCommit(commit);
		commit.addDocument(document);
		
		TraceableItemBean traceableItem = new TraceableItemBean();
		traceableItem.setItemTag("tag");
		traceableItem.setContent("content");
		traceableItem.setDocument(document);
		document.addTraceableItem(traceableItem);
		
		traceableItem.setDownstreamItems(Arrays.asList(traceableItem));
		traceableItem.setUpstreamItems(Arrays.asList(traceableItem));
		
		/*
		 * Both works. Can go with each way.
		 */
//		repositoryRepository.saveAndFlush(repository);
		commitRepository.saveAndFlush(commit);
		
		/*
		 * Confirmed by printed out SQL that database query is re-triggered (rather
		 * than just load the value from ORM caching).
		 */
		RepositoryBean reloadRepository = repositoryRepository.findById(1).get();
		assertEquals(reloadRepository.getCommitCount(), 1);
		
		/*
		 * Can't use `findById`, because the ID is changing for multiple run of
		 * this test.
		 */
		CommitBean reloadCommit = commitRepository.findByRepositoryIdAndCommitSha(1, "c36a5aed6e1c9f6a6c59bb21288a9d0bdbe93b73").get(0);
		assertTrue(reloadCommit instanceof ValidCommitBean);
		ValidCommitBean reloadValidCommit = (ValidCommitBean)reloadCommit;
		
		DocumentBean reloadDocument = reloadValidCommit.getDocument("file");
		assertEquals(reloadDocument.getName(), "file");
		assertNull(reloadValidCommit.getDocument("file-does-not-exist"));
		
		assertEquals(reloadDocument.getTraceableItems().size(), 1);
		TraceableItemBean reloadTraceableItem = reloadDocument.getTraceableItems().get(0);
		assertEquals(reloadTraceableItem.getItemTag(), "tag");
		assertEquals(reloadTraceableItem.getContent(), "content");
		
		assertEquals(reloadTraceableItem.getDownstreamItems().size(), 1);
		assertEquals(reloadTraceableItem.getUpstreamItems().size(), 1);
		assertEquals(reloadTraceableItem.getDownstreamItems().get(0), reloadTraceableItem);
		assertEquals(reloadTraceableItem.getUpstreamItems().get(0), reloadTraceableItem);
	}
	
	@Test
	@Transactional
	@DatabaseSetup(connection="schemaAuthDatabaseConnection", value="classpath:dbunit/minimal/auth.xml")
	@DatabaseTearDown
	public void testSaveAndFlushWorksMoreComplicatedTraceabilityMap() throws IOException, GitAPIException {
		
		assertFalse(commitRepository.findById(1).isPresent());
		
		RepositoryBean repository = repositoryRepository.findById(1).get();
		assertEquals(repository.getCommitCount(), 0);
		
		ValidCommitBean commit = new ValidCommitBean();
		commit.setSha("c36a5aed6e1c9f6a6c59bb21288a9d0bdbe93b73");
		commit.setRepository(repository);
		repository.addCommit(commit);
		
		DocumentBean document = new DocumentBean();
		document.setRelativePath("file");
		document.setCommit(commit);
		commit.addDocument(document);
		
		/*
		 * This part doesn't match with the static git repo, 
		 * but that doesn't since the repository is not actively
		 * load this part.
		 */
		TraceableItemBean traceableItem1 = new TraceableItemBean();
		traceableItem1.setItemTag("tag-1");
		traceableItem1.setContent("content-1");
		traceableItem1.setDocument(document);
		document.addTraceableItem(traceableItem1);
		
		TraceableItemBean traceableItem2 = new TraceableItemBean();
		traceableItem2.setItemTag("tag-2");
		traceableItem2.setContent("content-2");
		traceableItem2.setDocument(document);
		document.addTraceableItem(traceableItem2);
		
		traceableItem1.setDownstreamItems(Arrays.asList(traceableItem2));
		traceableItem2.setUpstreamItems(Arrays.asList(traceableItem1));
		
//		repositoryRepository.saveAndFlush(repository);
		commitRepository.saveAndFlush(commit);
		
		RepositoryBean reloadRepository = repositoryRepository.findById(1).get();
		assertEquals(reloadRepository.getCommitCount(), 1);
		
		CommitBean reloadCommit = commitRepository.findByRepositoryIdAndCommitSha(1, "c36a5aed6e1c9f6a6c59bb21288a9d0bdbe93b73").get(0);
		assertTrue(reloadCommit instanceof ValidCommitBean);
		ValidCommitBean reloadValidCommit = (ValidCommitBean)reloadCommit;
		
		DocumentBean reloadDocument = reloadValidCommit.getDocument("file");
		assertEquals(reloadDocument.getName(), "file");
		assertNull(reloadValidCommit.getDocument("file-does-not-exist"));
		
		assertEquals(reloadDocument.getTraceableItems().size(), 2);
		TraceableItemBean reloadTraceableItem1 = reloadDocument.getTraceableItems().get(0);
		assertEquals(reloadTraceableItem1.getItemTag(), "tag-1");
		assertEquals(reloadTraceableItem1.getContent(), "content-1");
		TraceableItemBean reloadTraceableItem2 = reloadDocument.getTraceableItems().get(1);
		assertEquals(reloadTraceableItem2.getItemTag(), "tag-2");
		assertEquals(reloadTraceableItem2.getContent(), "content-2");
		
		assertEquals(reloadTraceableItem1.getDownstreamItems().size(), 1);
		assertEquals(reloadTraceableItem1.getUpstreamItems().size(), 0);
		assertEquals(reloadTraceableItem1.getDownstreamItems().get(0), reloadTraceableItem2);
		assertEquals(reloadTraceableItem2.getDownstreamItems().size(), 0);
		assertEquals(reloadTraceableItem2.getUpstreamItems().size(), 1);
		assertEquals(reloadTraceableItem2.getUpstreamItems().get(0), reloadTraceableItem1);
	}
}
