package enterovirus.gitar;

import java.io.File;
import java.io.IOException;

import enterovirus.gitar.wrap.BranchName;
import enterovirus.gitar.wrap.CommitSha;

public class GitDocument extends GitTextFile {
	
	public GitDocument (File repositoryDirectory, CommitSha commitSha, String relativeFilepath) throws IOException {
		super(repositoryDirectory, commitSha, relativeFilepath);
	}
	
	public GitDocument (File repositoryDirectory, BranchName branchName, String relativeFilepath) throws IOException {
		super(repositoryDirectory, branchName, relativeFilepath);
	}
	
	public GitDocument (File repositoryDirectory, String relativeFilepath) throws IOException {
		super(repositoryDirectory, relativeFilepath);
	}
	
	/**
	 * TODO:
	 * An up-casting which figure out the traceable items from GitTextFile.
	 */
//	public GitTextFile (GitBlob gitBlob) throws IOException {
//		this.blobContent = gitBlob.blobContent;
//	}
}