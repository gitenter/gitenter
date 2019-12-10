package com.gitenter.protease.dao.git;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.Arrays;

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

import com.gitenter.protease.ProteaseConfig;
import com.gitenter.protease.dao.auth.RepositoryRepository;
import com.gitenter.protease.domain.auth.RepositoryBean;
import com.gitenter.protease.domain.git.CommitBean;
import com.gitenter.protease.domain.git.DocumentBean;
import com.gitenter.protease.domain.git.FileType;
import com.gitenter.protease.domain.git.ValidCommitBean;
import com.gitenter.protease.domain.traceability.TraceableDocumentBean;
import com.gitenter.protease.domain.traceability.TraceableItemBean;
import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;
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
public class CommitRepositoryTest {
	
	@Autowired CommitRepository commitRepository;
	
	@Autowired RepositoryRepository repositoryRepository;
	@Autowired IncludeFileRepository documentRepository;

	/*
	 * Cannot include DbUnit for `review` because otherwise tearDown will error out
	 * > Caused by: org.postgresql.util.PSQLException: ERROR: insert or update on table "subsection" violates foreign key constraint "subsection_id_fkey"
	 * > Detail: Key (id)=(1) is not present in table "valid_commit".
	 */
	@Test
	@DatabaseSetup(connection="schemaAuthDatabaseConnection", value="classpath:dbunit/minimal/auth.xml")
	@DatabaseSetup(connection="schemaGitDatabaseConnection", value="classpath:dbunit/minimal/git.xml")
	@DatabaseTearDown(connection="schemaGitDatabaseConnection", value="classpath:dbunit/minimal/git.xml")
	@DatabaseTearDown(connection="schemaAuthDatabaseConnection", value="classpath:dbunit/minimal/auth.xml")
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
	@DatabaseTearDown(connection="schemaAuthDatabaseConnection", value="classpath:dbunit/minimal/auth.xml")
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
	@DatabaseTearDown(connection="schemaAuthDatabaseConnection", value="classpath:dbunit/minimal/auth.xml")
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
		document.setFileType(FileType.MARKDOWN);
		document.setCommit(commit);
		commit.addIncludeFile(document);
		
		TraceableDocumentBean traceableDocument = new TraceableDocumentBean();
		traceableDocument.setDocument(document);
		document.setTraceableDocument(traceableDocument);
		
		TraceableItemBean traceableItem = new TraceableItemBean();
		traceableItem.setItemTag("tag");
		traceableItem.setContent("content");
		traceableItem.setTraceableDocument(traceableDocument);
		traceableDocument.addTraceableItem(traceableItem);
		
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
		
		DocumentBean reloadDocument = (DocumentBean)reloadValidCommit.getIncludeFile("file");
		assertEquals(reloadDocument.getName(), "file");
		assertNull(reloadValidCommit.getIncludeFile("file-does-not-exist"));
		
		TraceableDocumentBean reloadTraceableDocument = reloadDocument.getTraceableDocument();
		assertEquals(reloadTraceableDocument.getTraceableItems().size(), 1);
		TraceableItemBean reloadTraceableItem = reloadTraceableDocument.getTraceableItems().get(0);
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
	@DatabaseTearDown(connection="schemaAuthDatabaseConnection", value="classpath:dbunit/minimal/auth.xml")
	public void testSaveAndFlushWorksMoreThanOneTraceableItems() throws IOException, GitAPIException {
		
		assertFalse(commitRepository.findById(1).isPresent());
		
		RepositoryBean repository = repositoryRepository.findById(1).get();
		assertEquals(repository.getCommitCount(), 0);
		
		ValidCommitBean commit = new ValidCommitBean();
		commit.setSha("c36a5aed6e1c9f6a6c59bb21288a9d0bdbe93b73");
		commit.setRepository(repository);
		repository.addCommit(commit);
		
		DocumentBean document = new DocumentBean();
		document.setRelativePath("file");
		document.setFileType(FileType.MARKDOWN);
		document.setCommit(commit);
		commit.addIncludeFile(document);
		
		TraceableDocumentBean traceableDocument = new TraceableDocumentBean();
		traceableDocument.setDocument(document);
		document.setTraceableDocument(traceableDocument);
		
		/*
		 * This part doesn't match with the static git repo, 
		 * but that doesn't since the repository is not actively
		 * load this part.
		 */
		TraceableItemBean traceableItem1 = new TraceableItemBean();
		traceableItem1.setItemTag("tag-1");
		traceableItem1.setContent("content-1");
		traceableItem1.setTraceableDocument(traceableDocument);
		traceableDocument.addTraceableItem(traceableItem1);
		
		TraceableItemBean traceableItem2 = new TraceableItemBean();
		traceableItem2.setItemTag("tag-2");
		traceableItem2.setContent("content-2");
		traceableItem2.setTraceableDocument(traceableDocument);
		traceableDocument.addTraceableItem(traceableItem2);
		
		traceableItem1.setDownstreamItems(Arrays.asList(traceableItem2));
		traceableItem2.setUpstreamItems(Arrays.asList(traceableItem1));
		
//		repositoryRepository.saveAndFlush(repository);
		commitRepository.saveAndFlush(commit);
		
		RepositoryBean reloadRepository = repositoryRepository.findById(1).get();
		assertEquals(reloadRepository.getCommitCount(), 1);
		
		CommitBean reloadCommit = commitRepository.findByRepositoryIdAndCommitSha(1, "c36a5aed6e1c9f6a6c59bb21288a9d0bdbe93b73").get(0);
		assertTrue(reloadCommit instanceof ValidCommitBean);
		ValidCommitBean reloadValidCommit = (ValidCommitBean)reloadCommit;
		
		DocumentBean reloadDocument = (DocumentBean)reloadValidCommit.getIncludeFile("file");
		assertEquals(reloadDocument.getName(), "file");
		assertNull(reloadValidCommit.getIncludeFile("file-does-not-exist"));
		
		TraceableDocumentBean reloadTraceableDocument = reloadDocument.getTraceableDocument();
		assertEquals(reloadTraceableDocument.getTraceableItems().size(), 2);
		TraceableItemBean reloadTraceableItem1 = reloadTraceableDocument.getTraceableItems().get(0);
		assertEquals(reloadTraceableItem1.getItemTag(), "tag-1");
		assertEquals(reloadTraceableItem1.getContent(), "content-1");
		TraceableItemBean reloadTraceableItem2 = reloadTraceableDocument.getTraceableItems().get(1);
		assertEquals(reloadTraceableItem2.getItemTag(), "tag-2");
		assertEquals(reloadTraceableItem2.getContent(), "content-2");
		
		assertEquals(reloadTraceableItem1.getDownstreamItems().size(), 1);
		assertEquals(reloadTraceableItem1.getUpstreamItems().size(), 0);
		assertEquals(reloadTraceableItem1.getDownstreamItems().get(0), reloadTraceableItem2);
		assertEquals(reloadTraceableItem2.getDownstreamItems().size(), 0);
		assertEquals(reloadTraceableItem2.getUpstreamItems().size(), 1);
		assertEquals(reloadTraceableItem2.getUpstreamItems().get(0), reloadTraceableItem1);
	}
	
	@Test
	@Transactional
	@DatabaseSetup(connection="schemaAuthDatabaseConnection", value="classpath:dbunit/minimal/auth.xml")
	@DatabaseTearDown(connection="schemaAuthDatabaseConnection", value="classpath:dbunit/minimal/auth.xml")
	public void testSaveAndFlushWorksMoreThanOneDocuments() throws IOException, GitAPIException {
		
		assertFalse(commitRepository.findById(1).isPresent());
		
		RepositoryBean repository = repositoryRepository.findById(1).get();
		assertEquals(repository.getCommitCount(), 0);
		
		ValidCommitBean commit = new ValidCommitBean();
		commit.setSha("c36a5aed6e1c9f6a6c59bb21288a9d0bdbe93b73");
		commit.setRepository(repository);
		repository.addCommit(commit);
		
		DocumentBean document1 = new DocumentBean();
		document1.setRelativePath("file1");
		document1.setFileType(FileType.MARKDOWN);
		document1.setCommit(commit);
		commit.addIncludeFile(document1);
		
		DocumentBean document2 = new DocumentBean();
		document2.setRelativePath("file2");
		document2.setFileType(FileType.MARKDOWN);
		document2.setCommit(commit);
		commit.addIncludeFile(document2);
		
		TraceableDocumentBean traceableDocument1 = new TraceableDocumentBean();
		traceableDocument1.setDocument(document1);
		document1.setTraceableDocument(traceableDocument1);
		
		TraceableDocumentBean traceableDocument2 = new TraceableDocumentBean();
		traceableDocument2.setDocument(document2);
		document2.setTraceableDocument(traceableDocument2);
		
		/*
		 * This part doesn't match with the static git repo, 
		 * but that doesn't since the repository is not actively
		 * load this part.
		 */
		TraceableItemBean traceableItem1 = new TraceableItemBean();
		traceableItem1.setItemTag("tag-1");
		traceableItem1.setContent("content-1");
		traceableItem1.setTraceableDocument(traceableDocument1);
		traceableDocument1.addTraceableItem(traceableItem1);
		
		TraceableItemBean traceableItem2 = new TraceableItemBean();
		traceableItem2.setItemTag("tag-2");
		traceableItem2.setContent("content-2");
		traceableItem2.setTraceableDocument(traceableDocument2);
		traceableDocument2.addTraceableItem(traceableItem2);
		
		traceableItem1.setDownstreamItems(Arrays.asList(traceableItem2));
		traceableItem2.setUpstreamItems(Arrays.asList(traceableItem1));
		
//		repositoryRepository.saveAndFlush(repository);
		commitRepository.saveAndFlush(commit);
		
		RepositoryBean reloadRepository = repositoryRepository.findById(1).get();
		assertEquals(reloadRepository.getCommitCount(), 1);
		
		CommitBean reloadCommit = commitRepository.findByRepositoryIdAndCommitSha(1, "c36a5aed6e1c9f6a6c59bb21288a9d0bdbe93b73").get(0);
		assertTrue(reloadCommit instanceof ValidCommitBean);
		ValidCommitBean reloadValidCommit = (ValidCommitBean)reloadCommit;
		
		DocumentBean reloadDocument1 = (DocumentBean)reloadValidCommit.getIncludeFile("file1");
		assertEquals(reloadDocument1.getName(), "file1");
		DocumentBean reloadDocument2 = (DocumentBean)reloadValidCommit.getIncludeFile("file2");
		assertEquals(reloadDocument2.getName(), "file2");
		
		TraceableDocumentBean reloadTraceableDocument1 = reloadDocument1.getTraceableDocument();
		assertEquals(reloadTraceableDocument1.getTraceableItems().size(), 1);
		TraceableItemBean reloadTraceableItem1 = reloadTraceableDocument1.getTraceableItems().get(0);
		assertEquals(reloadTraceableItem1.getItemTag(), "tag-1");
		assertEquals(reloadTraceableItem1.getContent(), "content-1");
		TraceableDocumentBean reloadTraceableDocument2 = reloadDocument2.getTraceableDocument();
		assertEquals(reloadTraceableDocument2.getTraceableItems().size(), 1);
		TraceableItemBean reloadTraceableItem2 = reloadTraceableDocument2.getTraceableItems().get(0);
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
