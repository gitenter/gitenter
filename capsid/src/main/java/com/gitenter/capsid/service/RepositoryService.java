package com.gitenter.capsid.service;

import java.io.IOException;
import java.util.List;

import org.eclipse.jgit.api.errors.GitAPIException;

import com.gitenter.protease.domain.auth.RepositoryBean;
import com.gitenter.protease.domain.auth.RepositoryUserMapBean;
import com.gitenter.protease.domain.auth.UserBean;
import com.gitenter.protease.domain.git.CommitBean;
import com.gitenter.protease.domain.git.FileBean;
import com.gitenter.protease.domain.git.IncludeFileBean;

public interface RepositoryService {

	public RepositoryBean getRepository(Integer repositoryId) throws IOException;
	
	public List<RepositoryUserMapBean> getProjectOrganizerMaps(RepositoryBean repository);
	public List<RepositoryUserMapBean> getEditorMaps(RepositoryBean repository);
	public List<RepositoryUserMapBean> getBlacklistMaps(RepositoryBean repository);
	public List<UserBean> getProjectOrganizers(RepositoryBean repository);
	public List<UserBean> getEditors(RepositoryBean repository);

	public CommitBean getCommitFromBranchName(
			Integer repositoryId, 
			String branchName) throws IOException, GitAPIException;
	public CommitBean getCommitFromSha(
			Integer repositoryId, 
			String commitSha) throws IOException, GitAPIException;
	
	public IncludeFileBean getIncludeFileFromCommitShaAndRelativePath(
			String commitSha, 
			String relativePath) throws IOException, GitAPIException;
	public IncludeFileBean getIncludeFileFromRepositoryIdAndBranchAndRelativePath(
			Integer repositoryId, 
			String branchName, 
			String relativePath) throws IOException, GitAPIException;
	
	public FileBean getFileFromRepositoryIdAndCommitShaAndRelativePath(
			Integer repositoryId, 
			String commitSha, 
			String relativePath) throws IOException, GitAPIException;
	public FileBean getFileFromRepositoryIdAndBranchAndRelativePath(
			Integer repositoryId, 
			String branchName, 
			String relativePath) throws IOException, GitAPIException;

}
