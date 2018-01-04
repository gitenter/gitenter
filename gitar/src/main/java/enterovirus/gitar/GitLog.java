package enterovirus.gitar;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

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
		Repository repository = getRepositoryFromDirectory(repositoryDirectory);
		Git git = new Git(repository);
		Iterable<RevCommit> logs = git.log().add(repository.resolve(branchName.getName())).call();
		buildCommitShas(logs);
	}
	
	/*
	 * The log of "newCommitSha" is inclusive, while it is of 
	 * "oldCommitSha" is exclusive.
	 * 
	 */
	public GitLog(File repositoryDirectory, BranchName branchName, CommitSha oldCommitSha, CommitSha newCommitSha) throws IOException, GitAPIException {
		
		Repository repository = getRepositoryFromDirectory(repositoryDirectory);
		Git git = new Git(repository);
		Iterable<RevCommit> logs = git.log().add(repository.resolve(branchName.getName())).call();
		buildCommitShas(logs, oldCommitSha, newCommitSha);
	}
	
	private Repository getRepositoryFromDirectory(File repositoryDirectory) throws IOException {
		
		FileRepositoryBuilder builder = new FileRepositoryBuilder();
		Repository repository = builder.setGitDir(repositoryDirectory).readEnvironment().findGitDir().build();
		return repository;
	}
	
	private void buildCommitShas (Iterable<RevCommit> logs) {
		
		for (RevCommit rev : logs) {
			commitInfos.add(new CommitInfo(rev));
		}
	}
	
	/*
	 * Seems for the original merge branch
	 */
	private void buildCommitShas (Iterable<RevCommit> logs, CommitSha oldCommitSha, CommitSha newCommitSha) {
	
		boolean find = false;
		for (RevCommit rev : logs) {
			
			if (oldCommitSha.getShaChecksumHash().equals(rev.getName())) {
				break;
			}
			
			if (find == false && newCommitSha.getShaChecksumHash().equals(rev.getName())) {
				find = true;
			}
			if (find == true) {
				commitInfos.add(new CommitInfo(rev));
			}
		}
	}

	public List<CommitInfo> getCommitInfos() {
		return commitInfos;
	}
}
