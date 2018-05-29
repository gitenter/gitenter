package enterovirus.gitar;

import java.io.IOException;

import org.eclipse.jgit.lib.Ref;

public class GitBranch {

	private final String name;
	private final GitRepository gitRepository;
	
	private Ref branch;
	
	public String getName() {
		return name;
	}

	public GitBranch(GitRepository gitRepository, String name) throws IOException {
		this.gitRepository = gitRepository;
		this.name = name;
		
		branch = gitRepository.repository.exactRef("refs/heads/"+name);
	}
	
	public GitCommit getHead() throws IOException {
		return new GitCommit(gitRepository, branch.getObjectId().getName());
	}
}
