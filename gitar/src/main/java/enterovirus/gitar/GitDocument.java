package enterovirus.gitar;

import java.io.IOException;

import org.eclipse.jgit.lib.ObjectId;

public class GitDocument extends GitBlob {
	
	public GitDocument (String repositoryPath, ObjectId commitId, String filePath) throws IOException {
		super(repositoryPath, commitId, filePath);
	}
	
	public GitDocument (String repositoryPath, String branchName, String filePath) throws IOException {
		super(repositoryPath, branchName, filePath);
	}
	
	public GitDocument (String repositoryPath, String filePath) throws IOException {
		super(repositoryPath, filePath);
	}
	
	public String getStringContent () {
		return new String(getBlobContent());
	}
	
	/*
	 * TODO: 
	 * 
	 * Split by "newline" which is compatible to Windows
	 * or Linux formats.
	 */
	public String[] getLinewiseContent () {
		return new String(getBlobContent()).split("\n");
	}

}