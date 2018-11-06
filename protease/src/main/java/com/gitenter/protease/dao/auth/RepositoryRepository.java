package com.gitenter.protease.dao.auth;

import java.util.List;
import java.util.Optional;

import com.gitenter.protease.dao.exception.RepositoryNameNotUniqueException;
import com.gitenter.protease.domain.auth.RepositoryBean;

public interface RepositoryRepository {

	public Optional<RepositoryBean> findById(Integer id);
	public List<RepositoryBean> findByOrganizationNameAndRepositoryName(String organizationName, String repositoryName);
	
	public RepositoryBean saveAndFlush(RepositoryBean repository) throws RepositoryNameNotUniqueException;
}
