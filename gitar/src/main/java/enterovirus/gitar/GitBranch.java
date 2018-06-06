package enterovirus.gitar;

import java.io.IOException;
import java.util.List;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.CheckoutConflictException;
import org.eclipse.jgit.api.errors.GitAPIException;
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
	
	public boolean exist() throws IOException, GitAPIException {
		
		try (Git git = repository.getJGitGit()) {
			List<Ref> call = git.branchList().call();
			for (Ref ref : call) {
				if (name.equals(ref.getName().split("/")[2])) {
					return true;
				}
			}
		}
		return false;
	}
	
	public GitCommit getHead() throws IOException {
		return new GitCommit(repository, jGitBranch.getObjectId().getName());
	}
}
