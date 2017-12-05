package enterovirus.gitar;

import java.io.File;
import java.io.IOException;

import enterovirus.gitar.identification.BranchName;
import enterovirus.gitar.identification.CommitSha;

public class GitTextFile extends GitBlob {
	
	public GitTextFile (File repositoryDirectory, CommitSha commitSha, String relativeFilepath) throws IOException {
		super(repositoryDirectory, commitSha, relativeFilepath);
	}
	
	public GitTextFile (File repositoryDirectory, BranchName branchName, String relativeFilepath) throws IOException {
		super(repositoryDirectory, branchName, relativeFilepath);
	}
	
	public GitTextFile (File repositoryDirectory, String relativeFilepath) throws IOException {
		super(repositoryDirectory, relativeFilepath);
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