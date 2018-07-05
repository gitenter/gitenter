package com.gitenter.domain.review;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

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
import com.gitenter.dao.git.DocumentRepository;
import com.gitenter.domain.git.DocumentBean;
import com.gitenter.protease.ProteaseConfig;
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
public class InReviewDocumentBeanTest {
	
	/*
	 * TODO:
	 * 
	 * Cannot go with e.g. "InReviewDocumentRepository", for the same reason
	 * that git data cannot be loaded as "SubsectionBean".
	 */
	@Autowired DocumentRepository repository;

	@Test
	@Transactional
	@DbUnitMinimalDataSetup
	public void testDbUnitMinimal() throws IOException, GitAPIException {
		
		DocumentBean item = repository.findById(1).get();
		assertTrue(item instanceof InReviewDocumentBean);
		
		InReviewDocumentBean document = (InReviewDocumentBean)item;
		
		/*
		 * TODO:
		 * Git data are not loaded for subsection.
		 */
		assertEquals(document.getSubsection().getReview().getVersionNumber(), "v1");

		assertEquals(document.getStatus(), ReviewStatus.APPROVED);
		
		assertEquals(document.getDiscussionTopics().size(), 2);
		for (DiscussionTopicBean discussionTopic : document.getDiscussionTopics()) {
			if (discussionTopic.getId().equals(1)) {
				/*
				 * The double-linked document is actually been git loaded, thanks for
				 * a singleton setup of Hibernate ORM.
				 */
				assertEquals(discussionTopic.getDocument().getRelativePath(), item.getRelativePath());
				assertEquals(discussionTopic.getDocument().getBlobContent(), item.getBlobContent());
				assertEquals(discussionTopic.getLineNumber(), new Integer(1));
				
				assertTrue(discussionTopic instanceof ReviewMeetingRecordBean);
				ReviewMeetingRecordBean record = (ReviewMeetingRecordBean)discussionTopic;
				
				assertEquals(record.getContent(), "review meeting record content");
			}
			else if (discussionTopic.getId().equals(2)) {		
				assertEquals(discussionTopic.getLineNumber(), new Integer(2));
				
				assertTrue(discussionTopic instanceof OnlineDiscussionTopicBean);
				OnlineDiscussionTopicBean onlineTopic = (OnlineDiscussionTopicBean)discussionTopic;
				
				assertEquals(onlineTopic.getComments().size(), 1);
				assertEquals(onlineTopic.getComments().get(0).getAttendee().getMember().getUsername(), "username");
				assertEquals(onlineTopic.getComments().get(0).getContent(), "comment content");
			}
		}
	}

}
