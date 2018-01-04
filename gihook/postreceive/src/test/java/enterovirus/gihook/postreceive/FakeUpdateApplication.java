package enterovirus.gihook.postreceive;

import java.io.File;
import java.io.IOException;

import org.springframework.context.annotation.ComponentScan;

import enterovirus.gitar.wrap.BranchName;
import enterovirus.gitar.wrap.CommitSha;

@ComponentScan(basePackages = {
		"enterovirus.protease",
		"enterovirus.gihook.postreceive"})
public class FakeUpdateApplication {
	
	public static void main (String[] args) throws IOException {

		File repositoryDirectory = new File("/home/beta/Workspace/enterovirus-test/hook-fake-update/org/repo.git");
		BranchName branchName = new BranchName("master");
		CommitSha oldCommitSha = new CommitSha(new File("/home/beta/Workspace/enterovirus-test/hook-fake-update/old_commit_sha.txt"), 1);
		CommitSha newCommitSha = new CommitSha(new File("/home/beta/Workspace/enterovirus-test/hook-fake-update/new_commit_sha.txt"), 1);;
	}
}
