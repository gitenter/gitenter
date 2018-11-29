package com.gitenter.envelope.service;

import org.springframework.security.core.Authentication;

import com.gitenter.envelope.dto.OrganizationDTO;
import com.gitenter.protease.domain.auth.OrganizationBean;

public interface OrganizationManagerService {
	
	public void updateOrganization(
			Authentication authentication, 
			OrganizationBean organizationBean, 
			OrganizationDTO organizationDTO);
	
	public void addOrganizationMember(OrganizationBean organization, String username);
	public void removeOrganizationMember(OrganizationBean organization, String username);
	
	public void addOrganizationManager(OrganizationBean organization, String username);
	public void removeOrganizationManager(OrganizationBean organization, String username);
}
