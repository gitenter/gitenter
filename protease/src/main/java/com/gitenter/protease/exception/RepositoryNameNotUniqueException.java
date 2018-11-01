package com.gitenter.protease.exception;

import com.gitenter.protease.domain.auth.RepositoryBean;

public class RepositoryNameNotUniqueException extends NameNotUniqueException {

	private static final long serialVersionUID = 1L;
	
	public RepositoryNameNotUniqueException(RepositoryBean repository) {
		super("Organizations can only have distinguishable repository names. "
				+ "Organization "+repository.getOrganization().getName()
				+ " already has a repository with name "+repository.getName()+".");
	}
}
