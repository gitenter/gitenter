package enterovirus.gitar;

import java.io.File;
import org.junit.Test;

import enterovirus.gitar.wrap.BranchName;
import enterovirus.gitar.wrap.CommitInfo;
import enterovirus.gitar.wrap.CommitSha;

public class GitLogTest {
	
	@Test
	public void test1() throws Exception {
		
		File repositoryDirectory = new File("/home/beta/Workspace/enterovirus-test/one-repo-fix-commit/org/repo.git");
		BranchName branchName = new BranchName("master");
		CommitSha oldCommitSha = new CommitSha("------");
		CommitSha newCommitSha = new CommitSha("------"); 
		
		GitLog gitLog = new GitLog(repositoryDirectory, branchName, oldCommitSha, newCommitSha);
		
		for (CommitInfo commitInfo : gitLog.getCommitInfos()) {
			System.out.println(commitInfo.getShaChecksumHash());
		}
	}

}