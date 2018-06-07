package enterovirus.gitar;

import java.io.IOException;

import org.eclipse.jgit.lib.Ref;

public class GitBranch {

	protected final String name;
	protected final GitRepository repository;
	
	private Ref jGitBranch;
	
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
}
