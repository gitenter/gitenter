package enterovirus.gitar;

import java.io.File;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import enterovirus.gitar.wrap.BranchName;
import enterovirus.gitar.wrap.CommitSha;

public class GitLogTest {
	
	File repositoryDirectory;
	
	@Before
	public void initialize() {
		repositoryDirectory = new File("/home/beta/Workspace/enterovirus-test/one-repo-fix-commit/org/repo.git");
	}
	
	@Test
	public void test1() throws Exception {
		GitLog gitLog = new GitLog(repositoryDirectory);
		
		for (CommitSha commitSha : gitLog.getCommitShas()) {
			System.out.println(commitSha.getShaChecksumHash());
		}
	}

}