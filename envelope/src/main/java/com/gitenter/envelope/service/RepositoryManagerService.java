package com.gitenter.envelope.service;

import java.io.IOException;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.security.core.Authentication;

import com.gitenter.envelope.dto.RepositoryDTO;
import com.gitenter.protease.domain.auth.MemberBean;
import com.gitenter.protease.domain.auth.OrganizationBean;
import com.gitenter.protease.domain.auth.RepositoryBean;

public interface RepositoryManagerService {

	public void createRepository(
			Authentication authentication, 
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
}
