package com.gitenter.capsid.service;

import java.io.IOException;

import org.eclipse.jgit.api.errors.GitAPIException;

import com.gitenter.capsid.dto.RepositoryDTO;
import com.gitenter.protease.domain.auth.MemberBean;
import com.gitenter.protease.domain.auth.OrganizationBean;
import com.gitenter.protease.domain.auth.RepositoryBean;

public interface RepositoryManagerService {

	public void createRepository(
			MemberBean me, 
			OrganizationBean organization, 
			RepositoryDTO repositoryDTO, 
			Boolean includeSetupFiles) throws IOException, GitAPIException;
	public void updateRepository(
			RepositoryBean repository, 
			RepositoryDTO repositoryDTO) throws IOException;
	
	public void addCollaborator(
			RepositoryBean repository, 
			MemberBean collaborator, 
			String roleName) throws IOException;
	public void removeCollaborator(
			RepositoryBean repository, 
			Integer repositoryMemberMapId) throws IOException;
	
	public void deleteRepository(RepositoryBean repository) throws IOException, GitAPIException;
}
