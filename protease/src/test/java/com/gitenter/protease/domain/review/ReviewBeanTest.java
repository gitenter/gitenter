package com.gitenter.protease.domain.review;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
import com.gitenter.protease.dao.review.ReviewRepository;
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
public class ReviewBeanTest {
	
	@Autowired ReviewRepository repository;

	@Test
	@Transactional
	@DbUnitMinimalDataSetup
	public void testDbUnitMinimal() {
		
		ReviewBean item = repository.findById(1).get();
		assertEquals(item.getVersionNumber(), "v1");
		assertEquals(item.getDescription(), "review description");
		assertEquals(item.getRepository().getName(), "repository");
		
		assertEquals(item.getAttendees().size(), 1);
		assertEquals(item.getAttendees().get(0).getPerson().getUsername(), "username");
		assertEquals(item.getAttendees().get(0).getReview().getVersionNumber(), "v1");
		
		assertEquals(item.getSubsections().size(), 1);
		SubsectionBean subsection = item.getSubsections().get(0);
		assertEquals(subsection.getRepository().getName(), "repository");
		/*
		 * TODO:
		 * Git information is not loaded.
		 */
//		assertEquals(subsection.getMessage(), "commit\n");
	}

}
