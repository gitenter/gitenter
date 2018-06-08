package enterovirus.gitar.temp;

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

	public GitLog(File repositoryDirectory, BranchName branchName, int maxCount, int skip) throws IOException, GitAPIException {
		
		/*
		 * The JGit function is compatible with branch name with the form
		 * "master" and "refs/heads/master".
		 */
		Repository repository = GitRepository.getRepositoryFromDirectory(repositoryDirectory);
		try (Git git = new Git(repository)) {
			Iterable<RevCommit> logs = git.log()
					.add(repository.resolve(branchName.getName()))
					.setMaxCount(maxCount)
					.setSkip(skip)
					.call();
			buildCommitShas(logs);
		}
	}
	
	/*
	 * The log of "newCommitSha" is inclusive, while it is of 
	 * "oldCommitSha" is exclusive.
	 */
	public GitLog(File repositoryDirectory, BranchName branchName, CommitSha oldCommitSha, CommitSha newCommitSha) throws IOException, GitAPIException {
		
		Repository repository = GitRepository.getRepositoryFromDirectory(repositoryDirectory);
		try (Git git = new Git(repository)) {
			
			if (oldCommitSha.isNull()) {
				/*
				 * This is for the case that when the "update"/"post-receive" hooks
				 * are triggered, but it is the very first commit with no previous
				 * commit exists.
				 * 
				 * Unfortunately, JGit doesn't have a method for that, so I need to
				 * iterate it by myself.
				 * 
				 * NOTE:
				 * 
				 * The problem is, for a new branch, although it starts from an existing
				 * commit, the "oldCommitSha" "update"/"post-receive" hooks provided
				 * is still a null value, so this log may contain several commits which
				 * shouldn't belong to this branch. However, there is no easy way to handle
				 * that in here (as "git log" itself has no knowledge on the knowledge
				 * of other branches, and/or the topology of commits. Methods includes 
				 * (1) Use "git log --graph" or JGit's "RevWalk"
				 * (2) Handle this problem somewhere else.
				 * 
				 * I am currently using the second method (in 
				 * "enterovirus.gihook.postreceive.UpdateDatabaseFromGit")
				 */
				Iterable<RevCommit> logs = git.log()
						.add(repository.resolve(branchName.getName()))
						.call();
				buildCommitShas(logs, newCommitSha);
			}
			else {
				ObjectId oldObjectId = ObjectId.fromString(oldCommitSha.getShaChecksumHash());
				ObjectId newObjectId = ObjectId.fromString(newCommitSha.getShaChecksumHash());
				
				Iterable<RevCommit> logs = git.log()
						.add(repository.resolve(branchName.getName()))
						.addRange(oldObjectId, newObjectId)
						.call();
				buildCommitShas(logs);
			}
		}
	}
	
	private void buildCommitShas (Iterable<RevCommit> logs) {
		
		for (RevCommit rev : logs) {
			commitInfos.add(new CommitInfo(rev));
		}
	}
	
	private void buildCommitShas (Iterable<RevCommit> logs, CommitSha newCommitSha) {
		boolean find = false;
		for (RevCommit rev : logs) {			
			if (find == false && newCommitSha.getShaChecksumHash().equals(rev.getName())) {
				find = true;
			}
			if (find == true) {
				/*
				 * So the "newCommitSha" is inclusive.
				 */
				commitInfos.add(new CommitInfo(rev));
			}
		}
	}

	public List<CommitInfo> getCommitInfos() {
		return commitInfos;
	}
	
	public List<CommitSha> getCommitShas() {
		
		List<CommitSha> commitShas = new ArrayList<CommitSha>();
		for (CommitInfo commitInfo : commitInfos) {
			commitShas.add(commitInfo.getCommitSha());
		}
		
		return commitShas;
	}
}
