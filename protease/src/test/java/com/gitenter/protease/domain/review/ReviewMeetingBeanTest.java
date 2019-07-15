package com.gitenter.protease.domain.review;

import static org.junit.Assert.assertEquals;

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
import com.gitenter.protease.dao.review.ReviewMeetingRepository;
import com.gitenter.protease.domain.review.ReviewMeetingBean;
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
public class ReviewMeetingBeanTest {
	
	@Autowired ReviewMeetingRepository repository;

	@Test
	@Transactional
	@DbUnitMinimalDataSetup
	public void testDbUnitMinimal() {
		
		ReviewMeetingBean item = repository.findById(1).get();
		
		/*
		 * TODO:
		 * 
		 * Subsection doesn't load git data.
		 */
		assertEquals(item.getSubsection().getRepository().getName(), "repository");
		
		assertEquals(item.getAttendees().size(), 1);
	}

}
