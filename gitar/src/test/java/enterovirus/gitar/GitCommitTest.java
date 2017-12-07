package enterovirus.gitar;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;

import javax.swing.tree.TreeNode;

import static org.junit.Assert.*;

import org.junit.Test;

import enterovirus.gitar.wrap.CommitSha;

public class GitCommitTest {

	@Test
	public void test() throws IOException {
		
		GitCommit gitCommit;
		
		File repositoryDirectory = new File("/home/beta/Workspace/enterovirus_data/user1/repo1/.git");
		CommitSha commitSha = new CommitSha("ff728f5674201025b9fc4ea76a0adde3323fb9fb"); 
		
		gitCommit = new GitCommit(repositoryDirectory, commitSha);
		
		showFolderStructure(gitCommit);
		showFolderStructure(gitCommit);
		
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
	
	public void showFolderStructure (GitCommit gitCommit) {
		showHierarchy(gitCommit.getFolderStructure());
	}
	
	private void showHierarchy (TreeNode parentNode) {
		
		System.out.println(parentNode);
		
		Enumeration e = parentNode.children();
		while(e.hasMoreElements()) {
			TreeNode node = (TreeNode)e.nextElement();
			showHierarchy(node);
		}
	}

}