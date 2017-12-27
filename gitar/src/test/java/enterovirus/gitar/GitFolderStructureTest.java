package enterovirus.gitar;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import enterovirus.gitar.wrap.BranchName;
import enterovirus.gitar.wrap.CommitSha;

public class GitFolderStructureTest {

	@Test
	public void test1() throws IOException {
		
		GitFolderStructure gitCommit;
		
		File repositoryDirectory = new File("/home/beta/Workspace/enterovirus_data/user1/repo1/.git");
		CommitSha commitSha = new CommitSha("841d9d8cb6c560f1efc4ff677b8c71362d71203c"); 
		
		gitCommit = new GitFolderStructure(repositoryDirectory, commitSha);
		
		System.out.println(gitCommit.getCommitSha().getShaChecksumHash());
		showFolderStructure(gitCommit);
	}

	@Test
	public void test2() throws IOException {
		
		GitFolderStructure gitCommit;
		
		File repositoryDirectory = new File("/home/beta/Workspace/enterovirus_data/user1/repo1/.git");
		BranchName branchName = new BranchName("master"); 
		
		gitCommit = new GitFolderStructure(repositoryDirectory, branchName);
		
		System.out.println(gitCommit.getCommitSha().getShaChecksumHash());
		showFolderStructure(gitCommit);
	}

	@Test
	public void test3() throws IOException {
		
		GitFolderStructure gitCommit;
		
		File repositoryDirectory = new File("/home/beta/Workspace/enterovirus_data/user1/repo1/.git");
		
		gitCommit = new GitFolderStructure(repositoryDirectory);
		
		System.out.println(gitCommit.getCommitSha().getShaChecksumHash());
		showFolderStructure(gitCommit);
	}
	
	private void showFolderStructure (GitFolderStructure gitCommit) {
		showHierarchy(gitCommit.getFolderStructure(), 0);
	}
	
	private void showHierarchy (GitFolderStructure.ListableTreeNode parentNode, int level) {
		
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
		
		for(GitFolderStructure.ListableTreeNode node : parentNode.childrenList()) {
			showHierarchy(node, level+1);
		}
	}
}