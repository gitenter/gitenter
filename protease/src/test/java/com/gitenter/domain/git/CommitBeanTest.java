package com.gitenter.domain.git;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

import com.gitenter.dao.auth.RepositoryRepository;
import com.gitenter.dao.git.CommitRepository;
import com.gitenter.domain.auth.RepositoryBean;
import com.gitenter.domain.auth.RepositoryMemberRole;
import com.gitenter.domain.git.BranchBean;
import com.gitenter.protease.*;
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
public class CommitBeanTest {

	@Autowired CommitRepository repository;
	
	@Test
	@Transactional
	@DatabaseSetup(connection="schemaAuthDatabaseConnection", value="classpath:dbunit/minimal/auth.xml")
	@DatabaseSetup(connection="schemaGitDatabaseConnection", value="classpath:dbunit/minimal/git.xml")
	public void testDbUnitMinimalQueryWorks() throws IOException, GitAPIException, ParseException {
		
		CommitBean item = repository.findById(1).get();
		
		assertEquals(item.getMessage(), "file\n");
//		assertTrue(
//				item.getTimestamp().getTime()
//				- new SimpleDateFormat("EEE MMM dd HH:mm:ss YYYY ZZZZZ").parse("Wed Jun 20 18:55:41 2018 -0400").getTime()
//				< 1000);
		assertEquals(item.getAuthor().getName(), "Cong-Xin Qiu");
		assertEquals(item.getAuthor().getEmailAddress(), "ozoox.o@gmail.com");
	}
	
	@Test
	@Transactional
	@DatabaseSetup(connection="schemaAuthDatabaseConnection", value="classpath:dbunit/minimal/auth.xml")
	@DatabaseSetup(connection="schemaGitDatabaseConnection", value="classpath:dbunit/minimal/git.xml")
	public void testMinimalFolderStructure() throws IOException, GitAPIException {
		
		CommitBean item = repository.findById(1).get();
		
		assert item instanceof CommitValidBean;
		CommitValidBean validItem = (CommitValidBean)item;
		
		FolderBean root = validItem.getRoot();
		assertEquals(root.getSubpath().size(), 1);
		
		PathBean subpath = root.getSubpath().iterator().next();
		assert subpath instanceof FileBean;
		assertEquals(subpath.getRelativePath(), "file");
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
}
