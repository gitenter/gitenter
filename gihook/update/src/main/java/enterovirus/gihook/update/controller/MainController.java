package enterovirus.gihook.update.controller;

import java.io.File;
import java.io.IOException;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;

import enterovirus.gitar.GitLog;
import enterovirus.gitar.GitSource;
import enterovirus.gitar.wrap.BranchName;
import enterovirus.gitar.wrap.CommitInfo;
import enterovirus.gitar.wrap.CommitSha;
import enterovirus.protease.database.CommitRepository;
import enterovirus.protease.database.RepositoryRepository;
import enterovirus.protease.domain.CommitBean;
import enterovirus.protease.domain.RepositoryBean;

@Component
public class MainController {
	
	@Autowired private RepositoryRepository repositoryRepository;
	@Autowired private CommitRepository commitRepository;

	private File repositoryDirectory;
	private BranchName branchName;
	private CommitSha oldCommitSha;
	private CommitSha newCommitSha;
	
	public MainController(File repositoryDirectory, BranchName branchName, CommitSha oldCommitSha, CommitSha newCommitSha) {
		
		this.repositoryDirectory = repositoryDirectory;
		this.branchName = branchName;
		this.oldCommitSha = oldCommitSha;
		this.newCommitSha = newCommitSha;
		
		/* 
		 * Change Java working directory to the hook folder of
		 * the corresponding fake git repository.
		 * 
		 * Or may set it up in Eclipse's "Run configuration".
		 */
		System.setProperty("user.dir", repositoryDirectory.getAbsolutePath());
		System.out.println("Current directory: "+System.getProperty("user.dir"));
	}

	public void updateGitCommits () throws IOException, GitAPIException {
		
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
}
