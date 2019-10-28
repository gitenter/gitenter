package com.gitenter.protease.domain.review;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
import com.gitenter.protease.dao.git.CommitRepository;
import com.gitenter.protease.domain.git.CommitBean;
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
public class SubsectionBeanTest {
	
	/*
	 * TODO:
	 * 
	 * Cannot go with e.g. "SubsectionRepository", as then the git material
	 * will not be loaded. Thinking about a better way of doing it, e.g., to
	 * make "CommitRepositoryImpl" be a generic class, and to let the actual
	 * CRUD repository to be its generic type.
	 */
	@Autowired CommitRepository repository;

	@Test
	@Transactional
	@DbUnitMinimalDataSetup
	public void testDbUnitMinimal() throws IOException, GitAPIException {
		
		CommitBean item = repository.findById(1).get();
		assertTrue(item instanceof DiscussionSubsectionBean);
		
		DiscussionSubsectionBean subsection = (DiscussionSubsectionBean)item;
		
		assertEquals(subsection.getReview().getVersionNumber(), "v1");
		assertEquals(subsection.getProjectOrganizer().getUsername(), "username");
		assertEquals(subsection.getInReviewDocuments().size(), 1);
		assertEquals(subsection.getReviewMeetings().size(), 1);
	}

}
