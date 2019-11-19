package com.gitenter.protease.domain.review;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;

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
import com.gitenter.protease.annotation.DbUnitMinimalDataSetup;
import com.gitenter.protease.dao.git.IncludeFileRepository;
import com.gitenter.protease.domain.git.DocumentBean;
import com.gitenter.protease.domain.git.IncludeFileBean;
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
public class InReviewDocumentBeanTest {
	
	/*
	 * TODO:
	 * 
	 * Cannot go with e.g. "InReviewDocumentRepository", for the same reason
	 * that git data cannot be loaded as "SubsectionBean".
	 */
	@Autowired IncludeFileRepository repository;

	@Test
	@Transactional
	@DbUnitMinimalDataSetup
	public void testDbUnitMinimal() throws IOException, GitAPIException {
		
		/*
		 * TODO:
		 * How Hibernate handles mixin, if two different subclasses extend it?
		 */
		IncludeFileBean item = repository.findById(1).get();
		assertTrue(item instanceof DocumentBean);
		
		DocumentBean document = (DocumentBean)item;
		InReviewDocumentBean inReviewDocument = document.getInReviewDocument();
		assertNotNull(inReviewDocument);
		
		/*
		 * TODO:
		 * Git data are not loaded for subsection.
		 */
		assertEquals(inReviewDocument.getSubsection().getReview().getVersionNumber(), "v1");

		assertEquals(inReviewDocument.getStatus(), ReviewStatus.APPROVED);
		
		assertEquals(inReviewDocument.getDiscussionTopics().size(), 2);
		for (DiscussionTopicBean discussionTopic : inReviewDocument.getDiscussionTopics()) {
			if (discussionTopic.getId().equals(1)) {
				/*
				 * The double-linked document is actually been git loaded, thanks for
				 * a singleton setup of Hibernate ORM.
				 */
				assertEquals(discussionTopic.getInReviewDocument().getDocument().getRelativePath(), item.getRelativePath());
				assertEquals(discussionTopic.getInReviewDocument().getDocument().getBlobContent(), item.getBlobContent());
				assertEquals(discussionTopic.getLineNumber(), Integer.valueOf(1));
				
				assertTrue(discussionTopic instanceof ReviewMeetingRecordBean);
				ReviewMeetingRecordBean record = (ReviewMeetingRecordBean)discussionTopic;
				
				assertEquals(record.getContent(), "review meeting record content");
			}
			else if (discussionTopic.getId().equals(2)) {		
				assertEquals(discussionTopic.getLineNumber(), Integer.valueOf(2));
				
				assertTrue(discussionTopic instanceof OnlineDiscussionTopicBean);
				OnlineDiscussionTopicBean onlineTopic = (OnlineDiscussionTopicBean)discussionTopic;
				
				assertEquals(onlineTopic.getComments().size(), 1);
				assertEquals(onlineTopic.getComments().get(0).getAttendee().getPerson().getUsername(), "username");
				assertEquals(onlineTopic.getComments().get(0).getContent(), "comment content");
			}
		}
	}
}
