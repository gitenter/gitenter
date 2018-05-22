package enterovirus.gitar;

import java.io.File;

import org.junit.Test;

import enterovirus.gitar.wrap.BranchName;

public class GitBranchTest {

	@Test
	public void test() throws Exception {
		File repositoryDirectory = new File(System.getProperty("user.home"), "Workspace/enterovirus-test/long_commit_path/org/repo.git");
		GitBranch gitBranch = new GitBranch(repositoryDirectory);
		for (BranchName name : gitBranch.getBranchNames()) {
			System.out.println(name.getName());
		}
	}
}