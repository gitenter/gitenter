package com.gitenter.protease.domain.git;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.text.ParseException;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.junit.Rule;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.rules.ExpectedException;
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
public class CommitBeanTest {

	@Autowired CommitRepository repository;
	
	@Rule public final ExpectedException exception = ExpectedException.none();
	
	@Test
	@Transactional
	@DbUnitMinimalDataSetup
	public void testDbUnitMinimalQueryWorksById() throws IOException, GitAPIException, ParseException {
		
		CommitBean item = repository.findById(1).get();
		
		assertEquals(item.getMessage(), "commit\n");
//		assertTrue(
//				item.getTimestamp().getTime()
//				- new SimpleDateFormat("EEE MMM dd HH:mm:ss YYYY ZZZZZ").parse("Wed Jun 20 18:55:41 2018 -0400").getTime()
//				< 1000);
		assertEquals(item.getAuthor().getName(), "Cong-Xin Qiu");
		assertEquals(item.getAuthor().getEmailAddress(), "ozoox.o@gmail.com");
		
		assertEquals(item.getRepository().getId(), Integer.valueOf(1));
		
		/*
		 * Currently if we `getRepository` from a commit object,
		 * the git related placeholders are not bootstrapped yet.
		 */
		assertThrows(NullPointerException.class, () -> {
			item.getRepository().getBranches();
		});
	}
	
	@Test
	@Transactional
	@DbUnitMinimalDataSetup
	public void testDbUnitMinimalQueryWorksByRepositoryIdAndSha() throws IOException, GitAPIException, ParseException {
		
		CommitBean item = repository.findByRepositoryIdAndCommitSha(1, "c36a5aed6e1c9f6a6c59bb21288a9d0bdbe93b73").get(0);	
		
		assertEquals(item.getMessage(), "commit\n");
		assertEquals(item.getAuthor().getName(), "Cong-Xin Qiu");
		assertEquals(item.getAuthor().getEmailAddress(), "ozoox.o@gmail.com");
		
		assertEquals(item.getRepository().getId(), Integer.valueOf(1));
	}
	
	@Test
	@Transactional
	@DbUnitMinimalDataSetup
	public void testMinimalFolderStructure() throws IOException, GitAPIException {
		
		CommitBean item = repository.findById(1).get();
		
		assert item instanceof ValidCommitBean;
		ValidCommitBean validItem = (ValidCommitBean)item;
		
		FolderBean root = validItem.getRoot();
		assertEquals(root.getSubpath().size(), 1);
		
		PathBean subpath = root.getSubpath().iterator().next();
		assert subpath instanceof FileBean;
		FileBean file = (FileBean)subpath;
		assertEquals(file.getRelativePath(), "file");
		assertEquals(file.getName(), "file");
		assertEquals(new String(file.getBlobContent()), "content");
	}
	
	@Test
	@Transactional
	@DbUnitMinimalDataSetup
	public void testMinimalFile() throws IOException, GitAPIException {
	
		CommitBean item = repository.findById(1).get();
		
		assert item instanceof ValidCommitBean;
		ValidCommitBean validItem = (ValidCommitBean)item;
		
		FileBean file = validItem.getFile("file");
		assertEquals(file.isFile(), true);
		assertEquals(file.isFolder(), false);
		assertEquals(file.getName(), "file");
		assertEquals(new String(file.getBlobContent()), "content");
	}
	
	@Test
	@Transactional
	@DbUnitMinimalDataSetup
	public void testMinimalDocument() throws IOException, GitAPIException {
	
		CommitBean item = repository.findById(1).get();
		
		assert item instanceof ValidCommitBean;
		ValidCommitBean validItem = (ValidCommitBean)item;
		
		IncludeFileBean file = validItem.getIncludeFile("file");
		assert file instanceof DocumentBean;
		DocumentBean document = (DocumentBean)file;
		assertEquals(document.getRelativePath(), "file");
		assertEquals(document.getName(), "file");
		assertEquals(new String(document.getBlobContent()), "content");
		
		assertEquals(document.getCommit().getId(), Integer.valueOf(1));
	}
	
	/*
	 * TODO:
	 * 
	 * Need to have a git repository with at least two commits by the same user.
	 */
//	@Test
//	@Transactional
//	@DatabaseSetup(connection="schemaAuthDatabaseConnection", value="classpath:dbunit/minimal/auth.xml")
//	@DatabaseSetup(connection="schemaGitDatabaseConnection", value="classpath:dbunit/minimal/git.xml")
//	public void testAuthorBeanShareObjectsWhenEmailIsTheSame() throws IOException, GitAPIException, ParseException {
//		
//		CommitBean item1 = repository.findById(1).get();
//		CommitBean item2 = repository.findById(1).get();
//		assertFalse(item1 == item2);
//		assertTrue(item1.getAuthor() == item2.getAuthor);
//	}
	
	/*
	 * TODO:
	 * Use a non-minimal setting to test the InvalidCommitBean and IgnoredCommitBean.
	 */
}
