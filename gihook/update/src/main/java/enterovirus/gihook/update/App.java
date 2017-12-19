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
	}
}
