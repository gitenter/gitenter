package com.gitenter.envelope.service;

import java.io.IOException;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.security.core.Authentication;

import com.gitenter.envelope.dto.RepositoryDTO;
import com.gitenter.protease.domain.auth.RepositoryBean;

public interface RepositoryManagerService {

	public void createRepository(Authentication authentication, Integer organizationId, RepositoryDTO repositoryDTO, Boolean includeSetupFiles) throws IOException, GitAPIException;
	public void updateRepository(Authentication authentication, RepositoryBean repositoryBean, RepositoryDTO repositoryDTO) throws IOException;
}
