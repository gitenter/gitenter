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
import com.gitenter.protease.dao.GitUpdateFactory;
import com.gitenter.protease.domain.git.DocumentBean;
import com.gitenter.protease.source.GitSource;

@Component
public class DocumentGitUpdateFactory implements GitUpdateFactory<DocumentBean> {

	@Autowired private GitSource gitSource;
	
	public void update(DocumentBean document) throws IOException, GitAPIException {

		File repositoryDirectory = gitSource.getBareRepositoryDirectory(
				document.getCommit().getRepository().getOrganization().getName(), 
				document.getCommit().getRepository().getName());
		
		GitRepository gitRepository = GitBareRepository.getInstance(repositoryDirectory);
		GitCommit gitCommit = gitRepository.getCommit(document.getCommit().getSha());
		GitHistoricalFile gitFile = gitCommit.getFile(document.getRelativePath());
		
		assert document.getRelativePath().equals(gitFile.getRelativePath());
		document.setFromGit(gitFile);

		/*
		 * TODO:
		 * But validCommit placeholders is not setup yet.
		 */
		assert document.getCommit().getSha().equals(gitCommit.getSha());
		document.getCommit().setFromGitCommit(gitCommit);
	}
}
