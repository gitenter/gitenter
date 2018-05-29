package enterovirus.gitar;

import java.io.IOException;

import org.eclipse.jgit.lib.Ref;

public class GitTag {

	private final String name;
	private final GitRepository gitRepository;
	
	private Ref tag;
	
	public String getName() {
		return name;
	}

	public GitTag(GitRepository gitRepository, String name) throws IOException {
		this.gitRepository = gitRepository;
		this.name = name;
		
		tag = gitRepository.repository.exactRef("refs/tags/"+name);
	}
	
	/*
	 * TODO:
	 * This method is really similar to GitBranch.getHead() .
	 * Consider to setup some structure later.
	 */
	public GitCommit getCommit() throws IOException {
		return new GitCommit(gitRepository, tag.getObjectId().getName());
	}
}
