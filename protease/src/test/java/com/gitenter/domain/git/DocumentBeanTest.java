package com.gitenter.domain.git;

import static org.junit.Assert.*;

import java.io.IOException;
import java.text.ParseException;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.junit.Before;
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

import com.gitenter.dao.git.DocumentRepository;
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
@DbUnitConfiguration(databaseConnection={"schemaAuthDatabaseConnection", "schemaGitDatabaseConnection"})
public class DocumentBeanTest {
	
	@Autowired DocumentRepository repository;
	
	@Test
	@Transactional
	@DatabaseSetup(connection="schemaAuthDatabaseConnection", value="classpath:dbunit/minimal/auth.xml")
	@DatabaseSetup(connection="schemaGitDatabaseConnection", value="classpath:dbunit/minimal/git.xml")
	public void testDbUnitMinimalQueryWorks() throws IOException, GitAPIException, ParseException {
		
		DocumentBean item = repository.findById(1).get();
		assertEquals(item.getName(), "file");
		assertEquals(item.getRelativePath(), "file");
		assertEquals(item.getContent(), "content");
		
		assertEquals(item.getCommit().getSha(), "c36a5aed6e1c9f6a6c59bb21288a9d0bdbe93b73");
	}
	
//	private DocumentBean document;
//
//	@Before
//	public void init() {
//		
//		CommitValidBean commit = new CommitValidBean();
//		String relativeFilepath = "/fake/path/to/a/document.md";
//		document = new DocumentBean(commit, relativeFilepath);
//		
//		TraceableItemBean traceableItem1 = new TraceableItemBean(document, "tag-1", "content-1");
//		TraceableItemBean traceableItem2 = new TraceableItemBean(document, "tag-2", "content-2");
//		document.addTraceableItem(traceableItem1);
//		document.addTraceableItem(traceableItem2);
//		
//		traceableItem1.addDownstreamItem(traceableItem2);
//		traceableItem2.addUpstreamItem(traceableItem1);
//		
//		document.buildTraceableItemIndex();
//	}
//	
//	@Test
//	public void test() {
//		
//		assertEquals("content-1", document.getTraceableItem("tag-1").getContent());
//		assertEquals("content-2", document.getTraceableItem("tag-2").getContent());
//		assertEquals("tag-2", document.getTraceableItem("tag-1").getDownstreamItems().get(0).getItemTag());
//		assertEquals("tag-1", document.getTraceableItem("tag-2").getUpstreamItems().get(0).getItemTag());
//	}
}