package com.gitenter.envelope.service;

import java.io.IOException;

import org.eclipse.jgit.api.errors.GitAPIException;

import com.gitenter.protease.domain.auth.RepositoryBean;
import com.gitenter.protease.domain.git.CommitBean;
import com.gitenter.protease.domain.git.DocumentBean;

public interface RepositoryService {

	public RepositoryBean getRepository(Integer repositoryId) throws IOException;

	public CommitBean getCommitFromBranchName(Integer repositoryId, String branchName) throws IOException, GitAPIException;
	public CommitBean getCommitFromSha(Integer repositoryId, String commitSha) throws IOException, GitAPIException;
	
	public DocumentBean getDocumentFromCommitShaAndRelativePath(String commitSha, String relativePath) throws IOException, GitAPIException;
	public DocumentBean getDocumentFromRepositoryIdAndBranchAndRelativeFilepath(Integer repositoryId, String branchName, String relativePath) throws IOException, GitAPIException;
}
