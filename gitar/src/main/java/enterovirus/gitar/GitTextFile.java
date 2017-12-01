package enterovirus.gitar;

import java.io.IOException;

import org.eclipse.jgit.lib.ObjectId;

public class GitTextFile extends GitBlob {
	
	public GitTextFile (String repositoryPath, GitCommit commit, String filePath) throws IOException {
		super(repositoryPath, commit, filePath);
	}
	
	public GitTextFile (String repositoryPath, GitBranch gitBranch, String filePath) throws IOException {
		super(repositoryPath, gitBranch, filePath);
	}
	
	public GitTextFile (String repositoryPath, String filePath) throws IOException {
		super(repositoryPath, filePath);
	}
	
	/**
	 * Upcasting.
	 */
//	public GitTextFile (GitBlob gitBlob) throws IOException {
//		this.blobContent = gitBlob.blobContent;
//	}
	
	public String getStringContent () {
		return new String(blobContent);
	}
	
	/*
	 * TODO: 
	 * 
	 * Split by "newline" which is compatible to Windows
	 * or Linux formats.
	 */
	public String[] getLinewiseContent () {
		return new String(blobContent).split("\n");
	}

}