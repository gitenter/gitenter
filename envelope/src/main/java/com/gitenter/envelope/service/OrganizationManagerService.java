package com.gitenter.envelope.service;

import java.io.IOException;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.security.core.Authentication;

import com.gitenter.envelope.dto.RepositoryDTO;

public interface OrganizationManagerService {
	
	public void createRepository(Authentication authentication, Integer organizationId, RepositoryDTO repositoryDTO, Boolean includeSetupFiles) throws IOException, GitAPIException;
}
