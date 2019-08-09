package com.gitenter.capsid.service.exception;

import com.gitenter.protease.domain.auth.RepositoryBean;

import lombok.Getter;

public class RepositoryNameNotUniqueException extends NameNotUniqueException {

	private static final long serialVersionUID = 1L;
	
	@Getter
	private final String shortMessage = "Repository name already exist!";
	
	public RepositoryNameNotUniqueException(RepositoryBean repository) {
		super("Organizations can only have distinguishable repository names. "
				+ "Organization "+repository.getOrganization().getName()
				+ " already has a repository with name "+repository.getName()+".");
	}
}
