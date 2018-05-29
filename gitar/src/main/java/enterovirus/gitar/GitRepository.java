package enterovirus.gitar;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import org.eclipse.jgit.api.errors.GitAPIException;

public abstract class GitRepository {
	
	protected File directory;
	
	public GitRepository(File directory) {
		this.directory = directory;
	}

	public File getDirectory() {
		return directory;
	}

	public void setDirectory(File directory) {
		this.directory = directory;
	}
	
	public GitCommit getCommit(String shaChecksumHash) {
		/*
		 * TODO:
		 * Do we need to check if this commit indeed exist?
		 */
		return new GitCommit(this, shaChecksumHash);
	}

	public abstract GitBranch getBranch(String branchName);
	public abstract Collection<GitBranch> getBranches();
	
	public abstract GitTag getTag(String tagName);
	
	public abstract Boolean addHook(File filepath);
	public abstract Boolean addHooks(File folderpath);
	
//	public abstract static GitRepository getFromFile(File folderpath);
}
