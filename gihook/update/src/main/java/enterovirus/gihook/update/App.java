package enterovirus.gihook.update;

import java.io.File;
import java.io.IOException;

import enterovirus.gitar.GitCommit;
import enterovirus.gitar.GitSource;
import enterovirus.gitar.wrap.CommitSha;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan
public class App {
	
	@Autowired Tmp tmp;
	
	private void run () {
		System.out.println("hello world");
		System.out.println(tmp.find());
	}
	
	public static void main (String[] args) throws IOException {
		
		ApplicationContext context = new AnnotationConfigApplicationContext(App.class);
		App p = context.getBean(App.class);
		p.run();
		
//		String branchName = args[0];
//		String oldCommitSha = args[1];
//		String newCommitSha = args[2];
//		
//		System.out.println("branchName: "+branchName);
//		System.out.println("oldCommitSha: "+oldCommitSha);
//		System.out.println("newCommitSha: "+newCommitSha);
//		
//		System.out.println("Current directory: "+System.getProperty("user.dir"));
//		
//		File repositoryDirectory = new File(System.getProperty("user.dir"));
//		CommitSha commitSha = new CommitSha(newCommitSha);
//		
//		String organizationName = GitSource.getOrganizationName(repositoryDirectory);
//		String repositoryName = GitSource.getRepositoryName(repositoryDirectory);
//		System.out.println(organizationName);
//		System.out.println(repositoryName);
//		
//		GitCommit commit = new GitCommit(repositoryDirectory, commitSha);
//		showFolderStructure(commit);
	}
	
//	private static void showFolderStructure (GitCommit gitCommit) {
//		showHierarchy(gitCommit.getFolderStructure(), 0);
//	}
//	
//	private static void showHierarchy (GitCommit.ListableTreeNode parentNode, int level) {
//		
//		for (int i = 0; i < level; ++i) {
//			System.out.print("\t");
//		}
//		System.out.println(parentNode);
//		
////		Enumeration<TreeNode> e = parentNode.children();
//////		while(e.hasMoreElements()) {
//////			TreeNode node = e.nextElement();
//////			showHierarchy(node);
//////		}		
////		for(TreeNode node : Collections.list(e)) {
////			showHierarchy(node, level+1);
////		}
//		
//		for(GitCommit.ListableTreeNode node : parentNode.childrenList()) {
//			showHierarchy(node, level+1);
//		}
//	}
}
