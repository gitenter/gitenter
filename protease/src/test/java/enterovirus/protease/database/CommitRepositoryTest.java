package enterovirus.protease.database;

import java.io.File;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import enterovirus.gitar.GitFolderStructure;
import enterovirus.gitar.wrap.CommitSha;
import enterovirus.protease.*;
import enterovirus.protease.domain.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles(profiles = "one_repo_fix_commit")
@ContextConfiguration(classes={OneRepoFixCommitTestConfig.class})
public class CommitRepositoryTest {

	@Autowired private CommitRepository commitRepository;
	
	@Test
	@Transactional
	public void test1() throws Exception {
		CommitBean commit = commitRepository.findById(1);
		System.out.println("Organization: "+commit.getRepository().getOrganization().getName());
		System.out.println("Repository Name: "+commit.getRepository().getName());
		System.out.println("Commit SHA: "+commit.getShaChecksumHash());
		showHierarchy(commit.getFolderStructure(), 0);
	}

	@Test
	@Transactional
	public void test2() throws Exception {
		
		File commitRecordFile = new File("/home/beta/Workspace/enterovirus-test/one-repo-fix-commit/commit-sha-list.txt");
		CommitSha commitSha = new CommitSha(commitRecordFile, 1);
		
		CommitBean commit = commitRepository.findByShaChecksumHash(commitSha.getShaChecksumHash());
		System.out.println("Organization: "+commit.getRepository().getOrganization().getName());
		System.out.println("Repository Name: "+commit.getRepository().getName());
		System.out.println("Commit SHA: "+commit.getShaChecksumHash());
		showHierarchy(commit.getFolderStructure(), 0);
	}

	@Test
	@Transactional
	public void test3() throws Exception {
		CommitBean commit = commitRepository.findByRepositoryId(1);
		System.out.println("Organization: "+commit.getRepository().getOrganization().getName());
		System.out.println("Repository Name: "+commit.getRepository().getName());
		System.out.println("Commit SHA: "+commit.getShaChecksumHash());
		showHierarchy(commit.getFolderStructure(), 0);
//		System.out.println(commit.getFolderStructure().childrenList().size());
	}
	
	private void showHierarchy (GitFolderStructure.ListableTreeNode parentNode, int level) {
		
		for (int i = 0; i < level; ++i) {
			System.out.print("\t");
		}
		System.out.println(parentNode);
		
//		Enumeration e = parentNode.children();
//		while(e.hasMoreElements()) {
//			TreeNode node = (TreeNode)e.nextElement();
//			showHierarchy(node);
//		}
		
		for(GitFolderStructure.ListableTreeNode node : parentNode.childrenList()) {
			showHierarchy(node, level+1);
		}
	}
}
