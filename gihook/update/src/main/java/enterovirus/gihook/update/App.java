package enterovirus.gihook.update;

import java.io.File;
import java.io.IOException;

import enterovirus.gitar.GitCommit;
import enterovirus.gitar.wrap.CommitSha;

public class App {
	
	public static void main (String[] args) throws IOException {
		
		String branchName = args[0];
		String oldCommitSha = args[1];
		String newCommitSha = args[2];
		
		System.out.println("branchName: "+branchName);
		System.out.println("oldCommitSha: "+oldCommitSha);
		System.out.println("newCommitSha: "+newCommitSha);
		
		System.out.println("Current directory: "+System.getProperty("user.dir"));
		
		File repositoryDirectory = new File(System.getProperty("user.dir"));
		CommitSha commitSha = new CommitSha(newCommitSha);
		
		GitCommit commit = new GitCommit(repositoryDirectory, commitSha);
		showFolderStructure(commit);
	}
	
	private static void showFolderStructure (GitCommit gitCommit) {
		showHierarchy(gitCommit.getFolderStructure(), 0);
	}
	
	private static void showHierarchy (GitCommit.ListableTreeNode parentNode, int level) {
		
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
		
		for(GitCommit.ListableTreeNode node : parentNode.childrenList()) {
			showHierarchy(node, level+1);
		}
	}
}
