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
		repositoryDirectory = new File("/home/beta/Workspace/enterovirus-test/long_commit_path/org/repo.git");
		commitRecordFileMaster = new File("/home/beta/Workspace/enterovirus-test/long_commit_path/commit-sha-list-master.txt");
		commitRecordFileUnmergebranch = new File("/home/beta/Workspace/enterovirus-test/long_commit_path/commit-sha-list-unmergebranch.txt");
		commitRecordFileMergebranch = new File("/home/beta/Workspace/enterovirus-test/long_commit_path/commit-sha-list-mergebranch.txt");
	}
	
	
	@Test
	public void test1() throws Exception {

		BranchName branchName = new BranchName("master");
		CommitSha oldCommitSha = new CommitSha(commitRecordFileMaster, 1);
		CommitSha newCommitSha = new CommitSha(commitRecordFileMaster, 10); 
		
		GitLog gitLog = new GitLog(repositoryDirectory, branchName, oldCommitSha, newCommitSha);
		
		System.out.println("ref: "+branchName.getName());
		for (CommitInfo commitInfo : gitLog.getCommitInfos()) {
			System.out.println(commitInfo.getCommitSha().getShaChecksumHash());
			System.out.println(commitInfo.getCommitDate());
		}
	}
	
	@Test
	public void test2() throws Exception {

		/*
		 * TODO:
		 * Here seems have a bug that one more commit get printed
		 * (the "28f231f84b8fe74308e8e6ffba5a8fd164502c81" one).
		 * Maybe the JGit "addRange(AnyObjectId since, AnyObjectId until)"
		 * has a bug.
		 */
		BranchName branchName = new BranchName("refs/heads/master");
		CommitSha oldCommitSha = new CommitSha(commitRecordFileMaster, 2);
		CommitSha newCommitSha = new CommitSha(commitRecordFileMaster, 10); 
		
		GitLog gitLog = new GitLog(repositoryDirectory, branchName, oldCommitSha, newCommitSha);
		
		System.out.println("ref: "+branchName.getName()+" "+oldCommitSha.getShaChecksumHash()+" "+newCommitSha.getShaChecksumHash());
		for (CommitInfo commitInfo : gitLog.getCommitInfos()) {
			System.out.println(commitInfo.getCommitSha().getShaChecksumHash());
		}
	}
	
	@Test
	public void test3() throws Exception {

		BranchName branchName = new BranchName("refs/heads/master");
		CommitSha oldCommitSha = new CommitSha("0000000000000000000000000000000000000000");
		CommitSha newCommitSha = new CommitSha(commitRecordFileMaster, 10); 
		
		GitLog gitLog = new GitLog(repositoryDirectory, branchName, oldCommitSha, newCommitSha);
		
		System.out.println("ref: "+branchName.getName()+" "+oldCommitSha.getShaChecksumHash()+" "+newCommitSha.getShaChecksumHash());
		for (CommitInfo commitInfo : gitLog.getCommitInfos()) {
			System.out.println(commitInfo.getCommitSha().getShaChecksumHash());
		}
	}
}