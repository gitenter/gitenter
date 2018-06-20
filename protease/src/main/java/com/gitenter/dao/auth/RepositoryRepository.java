package com.gitenter.dao.auth;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.eclipse.jgit.api.errors.GitAPIException;

import com.gitenter.domain.auth.RepositoryBean;
import com.gitenter.domain.git.BranchBean;
import com.gitenter.domain.git.CommitBean;

public interface RepositoryRepository {

	public Optional<RepositoryBean> findById(Integer id) throws IOException;
	public List<RepositoryBean> findByOrganizationNameAndRepositoryName(String organizationName, String repositoryName) throws IOException;
	
	public RepositoryBean saveAndFlush(RepositoryBean repository);
	
//	public Collection<BranchBean> getBranches(RepositoryBean repository) throws IOException, GitAPIException;
//	public List<CommitBean> getLog(RepositoryBean repository, BranchBean branch, Integer maxCount, Integer skip) throws IOException, GitAPIException;
}
