package enterovirus.gitar;

import java.io.File;

import org.junit.Test;

import enterovirus.gitar.wrap.BranchName;

public class GitBranchTest {

	@Test
	public void test() throws Exception {
		File repositoryDirectory = new File("/home/beta/Workspace/enterovirus-test/long-commit-path/org/repo.git");
		GitBranch gitBranch = new GitBranch(repositoryDirectory);
		for (BranchName branchName : gitBranch.getBranchNames()) {
			System.out.println(branchName.getName());
		}
	}
}