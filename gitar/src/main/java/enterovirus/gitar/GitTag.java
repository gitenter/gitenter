package enterovirus.gitar;

import java.io.IOException;

import org.eclipse.jgit.lib.Ref;

public class GitTag {

	private final String name;
	private final GitRepository repository;
	
	private Ref jGitTag;
	
	public String getName() {
		return name;
	}

	GitTag(GitRepository repository, String name) throws IOException {
		this.repository = repository;
		this.name = name;
		
		jGitTag = repository.jGitRepository.exactRef("refs/tags/"+name);
	}
	
	/*
	 * TODO:
	 * This method is really similar to GitBranch.getHead() .
	 * Consider to setup some structure later.
	 */
	public GitCommit getCommit() throws IOException {
		return new GitCommit(repository, jGitTag.getObjectId().getName());
	}
}
