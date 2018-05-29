package enterovirus.gitar;

import java.io.File;
import java.io.IOException;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.junit.Test;

public class GitFolderTest {
	
	@Test
	public void test() throws IOException, GitAPIException {
		
		File directory = new File(System.getProperty("user.home"), "Workspace/enterovirus-test/one_repo_fix_commit/org/repo.git");
		
		GitRepository gitRepository = new BareRepository(directory);
		GitCommit gitCommit = gitRepository.getBranch("master").getHead();
		GitFolder gitFolder = gitCommit.getFolder("1st-commit-folder");
		
		showHierarchy(gitFolder.getFolderStructure(), 0);
	}
	
	private void showHierarchy(GitFolder.ListableTreeNode parentNode, int level) {
		
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
		
		for(GitFolder.ListableTreeNode node : parentNode.childrenList()) {
			showHierarchy(node, level+1);
		}
	}
	
}
