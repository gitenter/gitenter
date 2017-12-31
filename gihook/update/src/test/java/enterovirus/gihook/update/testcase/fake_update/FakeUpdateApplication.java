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

/*
 * This main class has nothing to do with unit tests.
 * If this package is used as a library rather than a
 * stand-alone executive jar, then this class is not
 * needed. 
 */
@ComponentScan(basePackages = {
		"enterovirus.protease.config",
		"enterovirus.protease.database",
		"enterovirus.protease.domain",
		"enterovirus.gihook.update.testcase.fake_update"})
public class FakeUpdateApplication {
	
//	@Autowired Tmp tmp;
	@Autowired RepositoryRepository repositoryRepository;
	@Autowired CommitRepository commitRepository;
	
	private void hook (BranchName branchName, CommitSha oldCommitSha, CommitSha newCommitSha) throws IOException {
//		System.out.println("hello world");
//		System.out.println(tmp.find());
		
		File repositoryDirectory = new File(System.getProperty("user.dir"));
		
		String organizationName = GitSource.getBareRepositoryOrganizationName(repositoryDirectory);
		String repositoryName = GitSource.getBareRepositoryName(repositoryDirectory);
		System.out.println("organizationName="+organizationName);
		System.out.println("repositoryName="+repositoryName);

		GitFolderStructure gitCommit = new GitFolderStructure(repositoryDirectory, newCommitSha);
		showFolderStructure(gitCommit);
		
		RepositoryBean repository = repositoryRepository.findByOrganizationNameAndRepositoryName(organizationName, repositoryName);
		CommitBean commitBean = new CommitBean(repository, newCommitSha);
		commitRepository.saveAndFlush(commitBean);
	}
	
	public static void main (String[] args) throws IOException {
		/* 
		 * Change Java working directory to the hook folder of
		 * the corresponding fake git repository.
		 * 
		 * Or may set it up in Eclipse's "Run configuration".
		 */
		System.setProperty("user.dir", "/home/beta/Workspace/enterovirus-test/hook-fake-update/org/repo.git");
		System.out.println("Current directory: "+System.getProperty("user.dir"));

		File repositoryDirectory = new File(System.getProperty("user.dir"));
		BranchName branchName = new BranchName("master");
		CommitSha oldCommitSha = new CommitSha(new File("/home/beta/Workspace/enterovirus-test/hook-fake-update/old_commit_sha.txt"), 1);
		CommitSha newCommitSha = new CommitSha(new File("/home/beta/Workspace/enterovirus-test/hook-fake-update/new_commit_sha.txt"), 1);;
		
		System.out.println("branchName: "+branchName);
		System.out.println("oldCommitSha: "+oldCommitSha.getShaChecksumHash());
		System.out.println("newCommitSha: "+newCommitSha.getShaChecksumHash());
		
		ApplicationContext context = new AnnotationConfigApplicationContext(FakeUpdateApplication.class);
		FakeUpdateApplication p = context.getBean(FakeUpdateApplication.class);
		p.hook(branchName, oldCommitSha, newCommitSha);
	}
	
	private void updateGitCommits (File repositoryDirectory, BranchName branchName, CommitSha oldCommitSha, CommitSha newCommitSha) throws IOException, GitAPIException {
		
		GitLog gitLog = new GitLog(repositoryDirectory, branchName, oldCommitSha, newCommitSha);
		
		String organizationName = GitSource.getBareRepositoryOrganizationName(repositoryDirectory);
		String repositoryName = GitSource.getBareRepositoryName(repositoryDirectory);
		RepositoryBean repository = repositoryRepository.findByOrganizationNameAndRepositoryName(organizationName, repositoryName);
		
		for (CommitInfo commitInfo : gitLog.getCommitInfos()) {
			
//			GitFolderStructure gitCommit = new GitFolderStructure(repositoryDirectory, commitInfo.getCommitSha());
//			showFolderStructure(gitCommit);
//			
			CommitBean commit = new CommitBean(repository, commitInfo.getCommitSha());
			repository.addCommit(commit);
		}
		
		repositoryRepository.saveAndFlush(repository);
	}
	
	/////////////////////////////////////////////////////////////////
	
	private static void showFolderStructure (GitFolderStructure gitCommit) {
		showHierarchy(gitCommit.getFolderStructure(), 0);
	}
	
	private static void showHierarchy (GitFolderStructure.ListableTreeNode parentNode, int level) {
		
		for (int i = 0; i < level; ++i) {
			System.out.print("\t");
		}
		System.out.println(parentNode);
		
		for(GitFolderStructure.ListableTreeNode node : parentNode.childrenList()) {
			showHierarchy(node, level+1);
		}
	}
}
