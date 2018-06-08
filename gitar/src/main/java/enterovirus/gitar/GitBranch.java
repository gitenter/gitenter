package enterovirus.gitar;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.revwalk.RevCommit;

public class GitBranch {

	protected final String name;
	protected final GitRepository repository;
	
	private final Ref jGitBranch;
	
	public String getName() {
		return name;
	}

	GitBranch(GitRepository repository, String name) throws IOException {
		this.repository = repository;
		this.name = name;
		
		jGitBranch = repository.getJGitRepository().exactRef("refs/heads/"+name);
	}
	
	public GitCommit getHead() throws IOException {
		return new GitCommit(repository, jGitBranch.getObjectId().getName());
	}
	
	/*
	 * Newest commit first.
	 */
	public List<GitCommit> getLog() throws IOException, NoHeadException, GitAPIException {
		
		try (Git git = repository.getJGitGit()) {
			Iterable<RevCommit> jGitCommits = git.log()
					.add(repository.getJGitRepository().resolve(name))
					.call();
			
			return buildCommits(jGitCommits);
		}
	}
	
	public List<GitCommit> getLog(Integer maxCount, Integer skip) throws IOException, NoHeadException, GitAPIException {
		
		try (Git git = repository.getJGitGit()) {
			Iterable<RevCommit> jGitCommits = git.log()
					.add(repository.getJGitRepository().resolve(name))
					.setMaxCount(maxCount)
					.setSkip(skip)
					.call();
			
			return buildCommits(jGitCommits);
		}
	}
	
	private List<GitCommit> buildCommits(Iterable<RevCommit> jGitCommits) {
		
		List<GitCommit> commits = new ArrayList<GitCommit>();
		for (RevCommit jGitCommit : jGitCommits) {
			commits.add(new GitCommit(repository, jGitCommit));
		}
		
		return commits;
	}
}
