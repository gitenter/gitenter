package enterovirus.gitar;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import javax.swing.tree.TreeNode;

import static org.junit.Assert.*;

import org.junit.Test;

import enterovirus.gitar.tree.DefaultListableMutableTreeNode;
import enterovirus.gitar.tree.ListableTreeNode;
import enterovirus.gitar.wrap.BranchName;
import enterovirus.gitar.wrap.CommitSha;

public class GitCommitTest {

	@Test
	public void test1() throws IOException {
		
		GitCommit gitCommit;
		
		File repositoryDirectory = new File("/home/beta/Workspace/enterovirus_data/user1/repo1/.git");
		CommitSha commitSha = new CommitSha("841d9d8cb6c560f1efc4ff677b8c71362d71203c"); 
		
		gitCommit = new GitCommit(repositoryDirectory, commitSha);
		
		System.out.println(gitCommit.getCommitSha().getShaChecksumHash());
		showFolderStructure(gitCommit);
//		showFolderStructure(gitCommit);
		
//		for (String path : gitCommit.getFolderpaths()) {
//			System.out.println(path);
//		}
		
//		for (String path : gitCommit.getFolderpaths()) {
//			System.out.println(path);
//		}		
//		
//		for (String path : gitCommit.getFilepaths()) {
//			System.out.println(path);
//		}
	}

	@Test
	public void test2() throws IOException {
		
		GitCommit gitCommit;
		
		File repositoryDirectory = new File("/home/beta/Workspace/enterovirus_data/user1/repo1/.git");
		BranchName branchName = new BranchName("master"); 
		
		gitCommit = new GitCommit(repositoryDirectory, branchName);
		
		System.out.println(gitCommit.getCommitSha().getShaChecksumHash());
		showFolderStructure(gitCommit);
	}

	@Test
	public void test3() throws IOException {
		
		GitCommit gitCommit;
		
		File repositoryDirectory = new File("/home/beta/Workspace/enterovirus_data/user1/repo1/.git");
		
		gitCommit = new GitCommit(repositoryDirectory);
		
		System.out.println(gitCommit.getCommitSha().getShaChecksumHash());
		showFolderStructure(gitCommit);
	}
	
	private void showFolderStructure (GitCommit gitCommit) {
		showHierarchy(gitCommit.getFolderStructure(), 0);
	}
	
	private void showHierarchy (ListableTreeNode parentNode, int level) {
		
		for (int i = 0; i < level; ++i) {
			System.out.print("\t");
		}
		System.out.println(parentNode);
		
//		Enumeration<TreeNode> e = parentNode.children();
////		while(e.hasMoreElements()) {
////			TreeNode node = e.nextElement();
////			showHierarchy(node);
////		}		
//		for(TreeNode node : Collections.list(e)) {
//			showHierarchy(node, level+1);
//		}
		
		for(ListableTreeNode node : parentNode.childrenList()) {
			showHierarchy(node, level+1);
		}
	}
}