package enterovirus.gitar;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;

import enterovirus.gitar.wrap.*;

public class GitBranch {

	private List<BranchName> branchNames = new ArrayList<BranchName>();
	
	public GitBranch(File repositoryDirectory) throws IOException, GitAPIException {
		
		Repository repository = GitRepository.getRepositoryFromDirectory(repositoryDirectory);
		try (Git git = new Git(repository)) {
			List<Ref> call = git.branchList().call();
			for (Ref ref : call) {
				/*
				 * Parse "refs/heads/master" and get the name "master".
				 * So for other branches.
				 */
				branchNames.add(new BranchName(ref.getName().split("/")[2]));
			}
		}
	}

	public List<BranchName> getBranchNames() {
		return branchNames;
	}
}
