package enterovirus.gitar;

import java.io.IOException;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.CheckoutConflictException;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRefNameException;
import org.eclipse.jgit.api.errors.RefAlreadyExistsException;
import org.eclipse.jgit.api.errors.RefNotFoundException;
import org.eclipse.jgit.lib.Ref;

public class GitBranch {

	private final String name;
	private final GitRepository repository;
	
	private Ref jGitBranch;
	
	public String getName() {
		return name;
	}

	GitBranch(GitRepository repository, String name) throws IOException {
		this.repository = repository;
		this.name = name;
		
		jGitBranch = repository.jGitRepository.exactRef("refs/heads/"+name);
	}
	
	public GitCommit getHead() throws IOException {
		return new GitCommit(repository, jGitBranch.getObjectId().getName());
	}
	
	public void checkoutTo () throws CheckoutConflictException, GitAPIException {
		try (Git git = new Git(repository.jGitRepository)) {
			git.checkout().setName(name).call();
		}
	}
}
