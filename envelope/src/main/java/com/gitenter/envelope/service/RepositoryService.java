package com.gitenter.envelope.service;

import java.io.IOException;

import org.eclipse.jgit.api.errors.GitAPIException;

import com.gitenter.protease.domain.auth.RepositoryBean;
import com.gitenter.protease.domain.git.CommitBean;

public interface RepositoryService {

	public RepositoryBean getRepository(Integer repositoryId) throws IOException;
	public CommitBean getCommitFromBranchName(Integer repositoryId, String branchName) throws IOException, GitAPIException;
	public CommitBean getCommitFromSha(Integer repositoryId, String commitSha) throws IOException, GitAPIException;
}
