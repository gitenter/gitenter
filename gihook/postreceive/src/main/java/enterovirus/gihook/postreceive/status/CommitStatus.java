package enterovirus.gihook.postreceive.status;

import java.io.File;

import enterovirus.gitar.GitSource;
import enterovirus.gitar.wrap.BranchName;
import enterovirus.gitar.wrap.CommitSha;

public class CommitStatus {

	private File repositoryDirectory;
	private BranchName branchName;
	private CommitSha oldCommitSha;
	private CommitSha newCommitSha;
	
	public CommitStatus(File repositoryDirectory, BranchName branchName, CommitSha oldCommitSha, CommitSha newCommitSha) {
		
		this.repositoryDirectory = repositoryDirectory;
		this.branchName = branchName;
		this.oldCommitSha = oldCommitSha;
		this.newCommitSha = newCommitSha;
		
		/* 
		 * Change Java working directory to the hook folder of
		 * the corresponding fake git repository.
		 * 
		 * Or may set it up in Eclipse's "Run configuration".
		 * 
		 * This is not needed for the final application (because 
		 * git hook is already running in the correctly directory
		 * location). But it is useful for testing purposes.
		 */
		System.setProperty("user.dir", repositoryDirectory.getAbsolutePath());
	}
	
	public File getRepositoryDirectory() {
		return repositoryDirectory;
	}

	public BranchName getBranchName() {
		return branchName;
	}

	public CommitSha getOldCommitSha() {
		return oldCommitSha;
	}

	public CommitSha getNewCommitSha() {
		return newCommitSha;
	}
	
	public String getOrganizationName() {
		return GitSource.getBareRepositoryOrganizationName(repositoryDirectory);
	}
	
	public String getRepositoryName () {
		return GitSource.getBareRepositoryName(repositoryDirectory);
	}
}
