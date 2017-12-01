package enterovirus.gitar;

import java.io.File;
import java.io.IOException;

public class GitTextFile extends GitBlob {
	
	public GitTextFile (File repositoryDirectory, GitCommit commit, String filePath) throws IOException {
		super(repositoryDirectory, commit, filePath);
	}
	
	public GitTextFile (File repositoryDirectory, GitBranch gitBranch, String filePath) throws IOException {
		super(repositoryDirectory, gitBranch, filePath);
	}
	
	public GitTextFile (File repositoryDirectory, String filePath) throws IOException {
		super(repositoryDirectory, filePath);
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