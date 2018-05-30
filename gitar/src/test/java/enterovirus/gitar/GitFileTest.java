package enterovirus.gitar;

import java.io.File;
import java.io.IOException;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.junit.Test;

public class GitFileTest {
	
	@Test
	public void test() throws IOException, GitAPIException {
		
		File directory = new File(System.getProperty("user.home"), "Workspace/enterovirus-test/one_repo_fix_commit/org/repo.git");
		
		GitRepository gitRepository = new GitRemoteRepository(directory);
		GitCommit gitCommit = gitRepository.getBranch("master").getHead();
		GitFile gitFile = gitCommit.getFile("1st-commit-file-to-be-change-in-the-2nd-commit");
		
		System.out.println(new String(gitFile.getBlobContent()));
	}
}
