package enterovirus.capsid.database;

import org.junit.runner.RunWith;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import enterovirus.capsid.domain.*;
import enterovirus.gitar.GitCommit;

/*
 * @DataJpaTest cannot @Autowired the DataSource, so I use 
 * annotation @SpringBootTest
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class CommitRepositoryTest {

	@Autowired
	private CommitRepository repository;
	
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
