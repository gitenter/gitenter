package com.gitenter.protease.dao.auth;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.eclipse.jgit.api.errors.GitAPIException;

import com.gitenter.protease.domain.auth.RepositoryBean;

public interface RepositoryRepository {

	public Optional<RepositoryBean> findById(Integer id);
	public List<RepositoryBean> findByOrganizationNameAndRepositoryName(String organizationName, String repositoryName);
	
	public RepositoryBean init(RepositoryBean repository) throws IOException, GitAPIException;
	public RepositoryBean update(RepositoryBean repository);
	
	public void delete(RepositoryBean repository) throws IOException, GitAPIException;
}
