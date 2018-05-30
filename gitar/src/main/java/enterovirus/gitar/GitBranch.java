package enterovirus.gitar;

import java.io.IOException;

import org.eclipse.jgit.lib.Ref;

public class GitBranch {

	private final String name;
	private final GitRepository repository;
	
	private Ref jGitBranch;
	
	public String getName() {
		return name;
	}

	public GitBranch(GitRepository repository, String name) throws IOException {
		this.repository = repository;
		this.name = name;
		
		jGitBranch = repository.jGitRepository.exactRef("refs/heads/"+name);
	}
	
	public GitCommit getHead() throws IOException {
		return new GitCommit(repository, jGitBranch.getObjectId().getName());
	}
}
