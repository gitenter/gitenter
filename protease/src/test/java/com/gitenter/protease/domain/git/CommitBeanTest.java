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
import com.gitenter.protease.dao.git.CommitRepository;
import com.gitenter.protease.domain.git.CommitBean;
import com.gitenter.protease.domain.git.DocumentBean;
import com.gitenter.protease.domain.git.FileBean;
import com.gitenter.protease.domain.git.FolderBean;
import com.gitenter.protease.domain.git.PathBean;
import com.gitenter.protease.domain.git.ValidCommitBean;
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
@DbUnitConfiguration(databaseConnection={"schemaAuthDatabaseConnection", "schemaGitDatabaseConnection", "schemaReviewDatabaseConnection"})
public class CommitBeanTest {

	@Autowired CommitRepository repository;
	
	@Test
	@Transactional
	@DbUnitMinimalDataSetup
	public void testDbUnitMinimalQueryWorks() throws IOException, GitAPIException, ParseException {
		
		CommitBean item = repository.findById(1).get();
		
		assertEquals(item.getMessage(), "commit\n");
//		assertTrue(
//				item.getTimestamp().getTime()
//				- new SimpleDateFormat("EEE MMM dd HH:mm:ss YYYY ZZZZZ").parse("Wed Jun 20 18:55:41 2018 -0400").getTime()
//				< 1000);
		assertEquals(item.getAuthor().getName(), "Cong-Xin Qiu");
		assertEquals(item.getAuthor().getEmailAddress(), "ozoox.o@gmail.com");
		
		assertEquals(item.getRepository().getId(), new Integer(1));
	}
	
	@Test
	@Transactional
	@DatabaseSetup(connection="schemaAuthDatabaseConnection", value="classpath:dbunit/minimal/auth.xml")
	@DatabaseSetup(connection="schemaGitDatabaseConnection", value="classpath:dbunit/minimal/git.xml")
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
	@DatabaseSetup(connection="schemaAuthDatabaseConnection", value="classpath:dbunit/minimal/auth.xml")
	@DatabaseSetup(connection="schemaGitDatabaseConnection", value="classpath:dbunit/minimal/git.xml")
	public void testMinimalFile() throws IOException, GitAPIException {
	
		CommitBean item = repository.findById(1).get();
		
		assert item instanceof ValidCommitBean;
		ValidCommitBean validItem = (ValidCommitBean)item;
		
		FileBean file = validItem.getFile("file");
		assertEquals(file.getName(), "file");
		assertEquals(new String(file.getBlobContent()), "content");
	}
	
	@Test
	@Transactional
	@DatabaseSetup(connection="schemaAuthDatabaseConnection", value="classpath:dbunit/minimal/auth.xml")
	@DatabaseSetup(connection="schemaGitDatabaseConnection", value="classpath:dbunit/minimal/git.xml")
	public void testMinimalDocument() throws IOException, GitAPIException {
	
		CommitBean item = repository.findById(1).get();
		
		assert item instanceof ValidCommitBean;
		ValidCommitBean validItem = (ValidCommitBean)item;
		
		DocumentBean file = validItem.getDocument("file");
		assertEquals(file.getRelativePath(), "file");
		assertEquals(file.getName(), "file");
		assertEquals(new String(file.getBlobContent()), "content");
		
		assertEquals(file.getCommit().getId(), new Integer(1));
		assertEquals(file.getTraceableItems().size(), 1);
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
