package com.gitenter.dao.auth;

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

import com.gitenter.dao.auth.RepositoryGitDAO;
import com.gitenter.dao.auth.RepositoryRepository;
import com.gitenter.domain.auth.RepositoryBean;
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
@DbUnitConfiguration(databaseConnection={"schemaAuthDatabaseConnection"})
public class RepositoryGitDAOTest {

	@Autowired private RepositoryRepository repositoryRepository;
	@Autowired private RepositoryGitDAO repositoryGitDAO;
	
	private RepositoryBean repository;
	
	@Before
	@DatabaseSetup(connection="schemaAuthDatabaseConnection", value="classpath:dbunit/minimal/auth.xml")
	public void init() throws Exception {
		repository = repositoryRepository.findById(1).get();
	}
	
	@Test
	public void test1() throws Exception {
//
//		BranchName branchName = new BranchName("master");
//		repositoryGitDAO.loadCommitLog(repository, branchName, 10, 0);
//		for (Map.Entry<CommitInfo,CommitBean> entry : repository.getCommitLogMap().entrySet()) {
//			
//			CommitInfo commitInfo = entry.getKey();
//			CommitBean commit = entry.getValue();
//			
//			System.out.println(commitInfo.getCommitSha().getShaChecksumHash());
//			System.out.println(commitInfo.getFullMessage());
//			System.out.println(commit.getClass());
//		}
	}
	
//	@Test
//	public void test2() throws Exception {
//		
//		repositoryGitDAO.loadBranchNames(repository);
//		for (BranchName name : repository.getBranchNames()) {
//			System.out.println(name.getName());
//		}
//	}

}
