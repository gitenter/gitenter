package enterovirus.proteinsistence.database;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import enterovirus.gitar.GitCommit;

import enterovirus.proteinsistence.ComponentScanConfig;
import enterovirus.proteinsistence.config.*;
import enterovirus.proteinsistence.domain.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={ComponentScanConfig.class,DatabaseConfig.class,GitConfig.class})
public class CommitRepositoryTest {

	@Autowired private CommitRepository repository;
	
	@Test
	@Transactional
	public void test1() throws Exception {
		CommitBean commit = repository.findById(6);
		System.out.println("Organization: "+commit.getRepository().getOrganization().getName());
		System.out.println("Repository Name: "+commit.getRepository().getName());
		System.out.println("Commit SHA: "+commit.getShaChecksumHash());
		showHierarchy(commit.getFolderStructure(), 0);
	}

	@Test
	@Transactional
	public void test2() throws Exception {
		CommitBean commit = repository.findByShaChecksumHash("ff728f5674201025b9fc4ea76a0adde3323fb9fb");
		System.out.println("Organization: "+commit.getRepository().getOrganization().getName());
		System.out.println("Repository Name: "+commit.getRepository().getName());
		System.out.println("Commit SHA: "+commit.getShaChecksumHash());
		showHierarchy(commit.getFolderStructure(), 0);
	}

	@Test
	@Transactional
	public void test3() throws Exception {
		CommitBean commit = repository.findByRepositoryId(1);
		System.out.println("Organization: "+commit.getRepository().getOrganization().getName());
		System.out.println("Repository Name: "+commit.getRepository().getName());
		System.out.println("Commit SHA: "+commit.getShaChecksumHash());
		showHierarchy(commit.getFolderStructure(), 0);
//		System.out.println(commit.getFolderStructure().childrenList().size());
	}
	
	private void showHierarchy (GitCommit.ListableTreeNode parentNode, int level) {
		
		for (int i = 0; i < level; ++i) {
			System.out.print("\t");
		}
		System.out.println(parentNode);
		
//		Enumeration e = parentNode.children();
//		while(e.hasMoreElements()) {
//			TreeNode node = (TreeNode)e.nextElement();
//			showHierarchy(node);
//		}
		
		for(GitCommit.ListableTreeNode node : parentNode.childrenList()) {
			showHierarchy(node, level+1);
		}
	}
}
