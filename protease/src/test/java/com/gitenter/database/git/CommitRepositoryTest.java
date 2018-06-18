package com.gitenter.database.git;

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

import com.gitenter.database.git.CommitGitDAO;
import com.gitenter.database.git.CommitRepository;
import com.gitenter.protease.ProteaseConfig;
import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DbUnitConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles(profiles = "production")
@ContextConfiguration(classes={ProteaseConfig.class})
@TestExecutionListeners({
	DependencyInjectionTestExecutionListener.class,
	DirtiesContextTestExecutionListener.class,
	TransactionalTestExecutionListener.class,
	DbUnitTestExecutionListener.class })
@DbUnitConfiguration(databaseConnection={
		"schemaSettingsDatabaseConnection",
		"schemaGitDatabaseConnection"})
public class CommitRepositoryTest {

	@Autowired private CommitRepository commitRepository;
	@Autowired private CommitGitDAO commitGitDAO;
	
	@Test
	public void test() {
		
	}

	/*
	 * TODO:
	 * Need to mock Git update.
	 */
//	@Test
//	@Transactional
//	@DatabaseSetup(connection="schemaSettingsDatabaseConnection", value="dbunit-data/minimal-schema-settings.xml")
//	@DatabaseSetup(connection="schemaGitDatabaseConnection", value="dbunit-data/minimal-schema-git.xml")
//	public void testDbUnit() throws Exception {
//		CommitBean commit = commitRepository.findById(1);
//		System.out.println("Organization: "+commit.getRepository().getOrganization().getName());
//		System.out.println("Repository Name: "+commit.getRepository().getName());
//		System.out.println("Commit SHA: "+commit.getShaChecksumHash());
//	}
//	
//	@Test
//	@Transactional
//	public void testFindById() throws Exception {
//		CommitBean commit = commitRepository.findById(1);
//		System.out.println("Organization: "+commit.getRepository().getOrganization().getName());
//		System.out.println("Repository Name: "+commit.getRepository().getName());
//		System.out.println("Commit SHA: "+commit.getShaChecksumHash());
//		commitGitDAO.loadFolderStructure((CommitValidBean)commit, new String[] {});
//		showHierarchy(((CommitValidBean)commit).getFolderStructure(), 0);
//	}
//
//	@Test
//	@Transactional
//	public void testFindByRepositoryIdAndCommitSha() throws Exception {
//		
//		File commitRecordFile = new File(System.getProperty("user.home"), "Workspace/enterovirus-test/one_repo_fix_commit/commit-sha-list.txt");
//		CommitSha commitSha = new CommitSha(commitRecordFile, 1);
//		
//		Integer repositoryId = 1; // There is only one single repository in this testcase. So its Id is "1".
//		CommitBean commit = commitRepository.findByRepositoryIdAndCommitSha(repositoryId, commitSha);
//		System.out.println("Organization: "+commit.getRepository().getOrganization().getName());
//		System.out.println("Repository Name: "+commit.getRepository().getName());
//		System.out.println("Commit SHA: "+commit.getShaChecksumHash());
//		commitGitDAO.loadFolderStructure((CommitValidBean)commit, new String[] {});
//		showHierarchy(((CommitValidBean)commit).getFolderStructure(), 0);
//	}
//	
//	@Test
//	@Transactional
//	public void testFindByRepositoryIdAndCommitShaIn() throws Exception {
//		
//		File commitRecordFile = new File(System.getProperty("user.home"), "Workspace/enterovirus-test/one_repo_fix_commit/commit-sha-list.txt");
//		List<CommitSha> commitShas = new ArrayList<CommitSha>();
//		commitShas.add(new CommitSha(commitRecordFile, 1));
//		commitShas.add(new CommitSha(commitRecordFile, 2));
//		
//		Integer repositoryId = 1; // There is only one single repository in this testcase. So its Id is "1".
//		List<CommitBean> commits = commitRepository.findByRepositoryIdAndCommitShaIn(repositoryId, commitShas);
//		for (CommitBean commit : commits) {
//			System.out.println("Organization: "+commit.getRepository().getOrganization().getName());
//			System.out.println("Repository Name: "+commit.getRepository().getName());
//			System.out.println("Commit SHA: "+commit.getShaChecksumHash());
//		}
//	}
//
//	@Test
//	@Transactional
//	public void testFindByRepositoryIdAndBranch() throws Exception {
//		CommitBean commit = commitRepository.findByRepositoryIdAndBranch(1, new BranchName("master"));
//		System.out.println("Organization: "+commit.getRepository().getOrganization().getName());
//		System.out.println("Repository Name: "+commit.getRepository().getName());
//		System.out.println("Commit SHA: "+commit.getShaChecksumHash());
//		commitGitDAO.loadFolderStructure((CommitValidBean)commit, new String[] {});
//		showHierarchy(((CommitValidBean)commit).getFolderStructure(), 0);
////		System.out.println(commit.getFolderStructure().childrenList().size());
//	}
//	
//	private void showHierarchy (GitFolderStructure.ListableTreeNode parentNode, int level) {
//		
//		for (int i = 0; i < level; ++i) {
//			System.out.print("\t");
//		}
//		System.out.println(parentNode);
//		
////		Enumeration e = parentNode.children();
////		while(e.hasMoreElements()) {
////			TreeNode node = (TreeNode)e.nextElement();
////			showHierarchy(node);
////		}
//		
//		for(GitFolderStructure.ListableTreeNode node : parentNode.childrenList()) {
//			showHierarchy(node, level+1);
//		}
//	}
}
