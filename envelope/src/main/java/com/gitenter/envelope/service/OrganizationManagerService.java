package com.gitenter.envelope.service;

import java.io.IOException;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.security.core.Authentication;

import com.gitenter.envelope.dto.RepositoryDTO;

public interface OrganizationManagerService {

	public void addOrganizationMember(Integer organizationId, String username);
	public void removeOrganizationMember(Integer organizationId, String username);
	
	public void addOrganizationManager(Integer organizationId, String username);
	public void removeOrganizationManager(Integer organizationId, String username);
	
	public void createRepository(Authentication authentication, Integer organizationId, RepositoryDTO repositoryDTO, Boolean includeSetupFiles) throws IOException, GitAPIException;
}
