package com.gitenter.protease.domain.git;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.text.ParseException;

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
import com.gitenter.protease.dao.git.IncludeFileRepository;
import com.gitenter.protease.domain.traceability.TraceableDocumentBean;
import com.gitenter.protease.domain.traceability.TraceableItemBean;
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
@DbUnitConfiguration(databaseConnection={
		"schemaAuthDatabaseConnection", 
		"schemaGitDatabaseConnection",
		"schemaTraceabilityDatabaseConnection",
		"schemaReviewDatabaseConnection"})
public class IncludeFileBeanTest {
	
	@Autowired IncludeFileRepository repository;
	
	@Test
	@Transactional
	@DbUnitMinimalDataSetup
	public void testDbUnitMinimalQueryWorks() throws IOException, GitAPIException, ParseException {
		
		IncludeFileBean includeFile = repository.findById(1).get();
		assertEquals(includeFile.getName(), "file");
		assertEquals(includeFile.getRelativePath(), "file");
		assertEquals(includeFile.getContent(), "content");
		
		assert includeFile instanceof DocumentBean;
		DocumentBean document = (DocumentBean)includeFile;
		assertEquals(document.getCommit().getId(), Integer.valueOf(1));
		
		/*
		 * TODO:
		 * Move this part to `com.gitent4er.protease.dao.traceability`.
		 */
		TraceableDocumentBean traceableDocument = document.getTraceableDocument();
		assertEquals(traceableDocument.getTraceableItems().size(), 1);
		TraceableItemBean traceableItem = traceableDocument.getTraceableItems().get(0);
		assertEquals(traceableItem.getItemTag(), "tag");
		assertEquals(traceableItem.getContent(), "content");
		assertEquals(traceableItem.getDownstreamItems().size(), 1);
		assertEquals(traceableItem.getUpstreamItems().size(), 1);
		assertEquals(traceableItem.getDownstreamItems().get(0).getItemTag(), traceableItem.getItemTag());
		assertEquals(traceableItem.getUpstreamItems().get(0).getItemTag(), traceableItem.getItemTag());
	}
	
	/*
	 * TODO:
	 * Should we remove the nontrivial constructor of "TraceableItemBean",
	 * and initialize "DocumentBean" only through the ORM (so
	 * "List<TraceableItemBean> traceableItems" is naturally initialized)?
	 */
	@Test
	public void testAddTraceableItem() {
		
		TraceableDocumentBean traceableDocument = new TraceableDocumentBean();
		
		TraceableItemBean traceableItem1 = new TraceableItemBean();
		traceableItem1.setTraceableDocument(traceableDocument);
		traceableItem1.setItemTag("tag-1");
		traceableItem1.setContent("content-1");
		traceableDocument.addTraceableItem(traceableItem1);
		
		TraceableItemBean traceableItem2 = new TraceableItemBean();
		traceableItem2.setTraceableDocument(traceableDocument);
		traceableItem2.setItemTag("tag-2");
		traceableItem2.setContent("content-2");
		traceableDocument.addTraceableItem(traceableItem2);
		
		traceableItem1.addDownstreamItem(traceableItem2);
		traceableItem2.addUpstreamItem(traceableItem1);
		
		assertEquals("content-1", traceableDocument.getTraceableItem("tag-1").getContent());
		assertEquals("content-2", traceableDocument.getTraceableItem("tag-2").getContent());
		assertEquals("tag-2", traceableDocument.getTraceableItem("tag-1").getDownstreamItems().get(0).getItemTag());
		assertEquals("tag-1", traceableDocument.getTraceableItem("tag-2").getUpstreamItems().get(0).getItemTag());
	}
}