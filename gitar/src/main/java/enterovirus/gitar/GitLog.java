package enterovirus.gitar;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;

import enterovirus.gitar.wrap.BranchName;
import enterovirus.gitar.wrap.CommitInfo;
import enterovirus.gitar.wrap.CommitSha;

public class GitLog {

	private List<CommitInfo> commitInfos = new ArrayList<CommitInfo>();

	/*
	 * TODO:
	 * Wrap "GitApiException"??
	 */
	public GitLog(File repositoryDirectory, BranchName branchName) throws IOException, GitAPIException {
		
		/*
		 * The JGit function is compatible with branch name with the form
		 * "master" and "refs/heads/master".
		 */
		Repository repository = GitRepository.getRepositoryFromDirectory(repositoryDirectory);
		try (Git git = new Git(repository)) {
			Iterable<RevCommit> logs = git.log()
					.add(repository.resolve(branchName.getName()))
					.call();
			buildCommitShas(logs);
		}
	}

	public GitLog(File repositoryDirectory, BranchName branchName, Integer maxCount) throws IOException, GitAPIException {
		
		/*
		 * The JGit function is compatible with branch name with the form
		 * "master" and "refs/heads/master".
		 */
		Repository repository = GitRepository.getRepositoryFromDirectory(repositoryDirectory);
		try (Git git = new Git(repository)) {
			Iterable<RevCommit> logs = git.log()
					.add(repository.resolve(branchName.getName()))
					.setMaxCount(maxCount)
					.call();
			buildCommitShas(logs);
		}
	}
	
	/*
	 * The log of "newCommitSha" is inclusive, while it is of 
	 * "oldCommitSha" is exclusive.
	 * 
	 */
	public GitLog(File repositoryDirectory, BranchName branchName, CommitSha oldCommitSha, CommitSha newCommitSha) throws IOException, GitAPIException {
		
		Repository repository = GitRepository.getRepositoryFromDirectory(repositoryDirectory);
		try (Git git = new Git(repository)) {
			
			ObjectId oldObjectId = ObjectId.fromString(oldCommitSha.getShaChecksumHash());
			ObjectId newObjectId = ObjectId.fromString(newCommitSha.getShaChecksumHash());
			
			Iterable<RevCommit> logs = git.log()
					.add(repository.resolve(branchName.getName()))
					.addRange(oldObjectId, newObjectId)
					.call();
			buildCommitShas(logs);
		}
	}
	
	private void buildCommitShas (Iterable<RevCommit> logs) {
		
		for (RevCommit rev : logs) {
			commitInfos.add(new CommitInfo(rev));
		}
	}

	public List<CommitInfo> getCommitInfos() {
		return commitInfos;
	}
}
