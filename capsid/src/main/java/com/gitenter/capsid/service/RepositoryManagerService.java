package com.gitenter.capsid.service;

import java.io.IOException;

import org.eclipse.jgit.api.errors.GitAPIException;

import com.gitenter.capsid.dto.RepositoryDTO;
import com.gitenter.protease.domain.auth.PersonBean;
import com.gitenter.protease.domain.auth.OrganizationBean;
import com.gitenter.protease.domain.auth.RepositoryBean;

public interface RepositoryManagerService {

	public void createRepository(
			PersonBean me, 
			OrganizationBean organization, 
			RepositoryDTO repositoryDTO, 
			Boolean includeSetupFiles) throws IOException, GitAPIException;
	public void updateRepository(
			RepositoryBean repository, 
			RepositoryDTO repositoryDTO) throws IOException;
	
	public void addCollaborator(
			RepositoryBean repository, 
			PersonBean collaborator, 
			String roleName) throws IOException;
	public void removeCollaborator(
			RepositoryBean repository, 
			Integer repositoryPersonMapId) throws IOException;
	
	public void deleteRepository(RepositoryBean repository) throws IOException, GitAPIException;
}
