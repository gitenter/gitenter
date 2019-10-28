package com.gitenter.protease.dao.git;

import java.io.File;
import java.io.IOException;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.gitenter.gitar.GitBareRepository;
import com.gitenter.gitar.GitCommit;
import com.gitenter.gitar.GitHistoricalFile;
import com.gitenter.gitar.GitRepository;
import com.gitenter.protease.config.bean.GitSource;
import com.gitenter.protease.dao.GitUpdateFactory;
import com.gitenter.protease.domain.git.IncludeFileBean;

@Component
public class IncludeFileGitUpdateFactory implements GitUpdateFactory<IncludeFileBean> {

	@Autowired private GitSource gitSource;
	
	public void update(IncludeFileBean includeFile) throws IOException, GitAPIException {

		File repositoryDirectory = gitSource.getBareRepositoryDirectory(
				includeFile.getCommit().getRepository().getOrganization().getName(), 
				includeFile.getCommit().getRepository().getName());
		
		GitRepository gitRepository = GitBareRepository.getInstance(repositoryDirectory);
		GitCommit gitCommit = gitRepository.getCommit(includeFile.getCommit().getSha());
		GitHistoricalFile gitFile = gitCommit.getFile(includeFile.getRelativePath());
		
		assert includeFile.getRelativePath().equals(gitFile.getRelativePath());
		includeFile.setFromGit(gitFile);

		/*
		 * TODO:
		 * But validCommit placeholders is not setup yet.
		 */
		assert includeFile.getCommit().getSha().equals(gitCommit.getSha());
		includeFile.getCommit().setFromGitCommit(gitCommit);
	}
}
