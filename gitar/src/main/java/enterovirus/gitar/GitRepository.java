package enterovirus.gitar;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;

import enterovirus.gitar.GitBranch;
import enterovirus.gitar.GitTag;

public abstract class GitRepository {
	
	protected File directory;
	Repository repository;

	public File getDirectory() {
		return directory;
	}

	public void setDirectory(File directory) {
		this.directory = directory;
	}
	
	public GitRepository(File directory) {
		this.directory = directory;
	}
	
	public GitCommit getCommit(String shaChecksumHash) throws IOException {
		return new GitCommit(this, shaChecksumHash);
	}

	public GitBranch getBranch(String branchName) throws IOException {
		return new GitBranch(this, branchName);
	}
	
	public Collection<GitBranch> getBranches() throws IOException, GitAPIException {
		
		Collection<GitBranch> branches = new ArrayList<GitBranch>();
		try (Git git = new Git(repository)) {
			List<Ref> call = git.branchList().call();
			for (Ref ref : call) {
				/*
				 * Parse "refs/heads/master" and get the name "master".
				 * So for other branches.
				 */
				branches.add(new GitBranch(this, ref.getName().split("/")[2]));
			}
		}
		
		return branches;
	}
	
	public GitTag getTag(String tagName) throws IOException {
		return new GitTag(this, tagName);
	}
	
	public abstract Boolean addHook(File filepath);
	public abstract Boolean addHooks(File folderpath);
	
//	public abstract static GitRepository getFromFile(File folderpath);
}
