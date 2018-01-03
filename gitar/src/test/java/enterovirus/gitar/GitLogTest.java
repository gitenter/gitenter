package enterovirus.gitar;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

import enterovirus.gitar.wrap.BranchName;
import enterovirus.gitar.wrap.CommitInfo;
import enterovirus.gitar.wrap.CommitSha;

public class GitLogTest {
	
	private File repositoryDirectory;
	private File commitRecordFileMaster;
	private File commitRecordFileUnmergebranch;
	private File commitRecordFileMergebranch;
	
	@Before
	public void initialize() {
		repositoryDirectory = new File("/home/beta/Workspace/enterovirus-test/long-commit-path/org/repo.git");
		commitRecordFileMaster = new File("/home/beta/Workspace/enterovirus-test/long-commit-path/commit-sha-list-master.txt");
		commitRecordFileUnmergebranch = new File("/home/beta/Workspace/enterovirus-test/long-commit-path/commit-sha-list-unmergebranch.txt");
		commitRecordFileMergebranch = new File("/home/beta/Workspace/enterovirus-test/long-commit-path/commit-sha-list-mergebranch.txt");
	}
	
	
	@Test
	public void test() throws Exception {

		BranchName branchName = new BranchName("master");
		CommitSha oldCommitSha = new CommitSha(commitRecordFileMaster, 1);
		CommitSha newCommitSha = new CommitSha(commitRecordFileMaster, 10); 
		
		GitLog gitLog = new GitLog(repositoryDirectory, branchName, oldCommitSha, newCommitSha);
		
		for (CommitInfo commitInfo : gitLog.getCommitInfos()) {
			System.out.println(commitInfo.getCommitSha().getShaChecksumHash());
		}
	}
}