package enterovirus.gihook.update.testcase.fake_update;

import java.io.File;
import java.io.IOException;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;

import enterovirus.gitar.GitFolderStructure;
import enterovirus.gitar.GitLog;
import enterovirus.gitar.GitSource;
import enterovirus.gitar.wrap.BranchName;
import enterovirus.gitar.wrap.CommitInfo;
import enterovirus.gitar.wrap.CommitSha;
import enterovirus.protease.database.*;
import enterovirus.protease.domain.*;

@ComponentScan(basePackages = {
		"enterovirus.protease.config",
		"enterovirus.protease.database",
		"enterovirus.protease.domain",
		"enterovirus.gihook.update.testcase.fake_update"})
public class FakeUpdateApplication {
	
	public static void main (String[] args) throws IOException {

		File repositoryDirectory = new File("/home/beta/Workspace/enterovirus-test/hook-fake-update/org/repo.git");
		BranchName branchName = new BranchName("master");
		CommitSha oldCommitSha = new CommitSha(new File("/home/beta/Workspace/enterovirus-test/hook-fake-update/old_commit_sha.txt"), 1);
		CommitSha newCommitSha = new CommitSha(new File("/home/beta/Workspace/enterovirus-test/hook-fake-update/new_commit_sha.txt"), 1);;
	}
}
