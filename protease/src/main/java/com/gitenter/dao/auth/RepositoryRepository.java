package com.gitenter.dao.auth;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import com.gitenter.domain.auth.RepositoryBean;

public interface RepositoryRepository {

	public Optional<RepositoryBean> findById(Integer id) throws IOException;
	public List<RepositoryBean> findByOrganizationNameAndRepositoryName(String organizationName, String repositoryName) throws IOException;
	
	public RepositoryBean saveAndFlush(RepositoryBean repository);
}
