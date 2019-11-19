package com.gitenter.capsid.service;

import java.io.IOException;

import org.springframework.security.core.Authentication;

import com.gitenter.capsid.dto.OrganizationDTO;
import com.gitenter.protease.domain.auth.UserBean;
import com.gitenter.protease.domain.auth.OrganizationBean;

public interface OrganizationManagerService {
	
	public void createOrganization(UserBean me, OrganizationDTO organizationDTO) throws IOException;
	
	public void updateOrganization(
			Authentication authentication, 
			OrganizationBean organizationBean, 
			OrganizationDTO organizationDTO) throws IOException;
	
	public void addOrganizationMember(OrganizationBean organization, UserBean user);
	public void removeOrganizationMember(
			OrganizationBean organization, 
			Integer organizationUserMapId) throws IOException;
	
	public void addOrganizationManager(
			OrganizationBean organization, 
			Integer organizationUserMapId) throws IOException;
	public void removeOrganizationManager(
			Authentication authentication, 
			OrganizationBean organization, 
			Integer organizatioUserMapId) throws IOException;
	
	public void deleteOrganization(OrganizationBean organization) throws IOException;
}
