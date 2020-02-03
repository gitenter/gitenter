package com.gitenter.capsid.service;

import java.io.IOException;

import org.springframework.security.core.Authentication;

import com.gitenter.capsid.dto.OrganizationDTO;
import com.gitenter.protease.domain.auth.UserBean;
import com.gitenter.protease.domain.auth.OrganizationBean;
import com.gitenter.protease.domain.auth.OrganizationUserMapBean;

public interface OrganizationManagerService {
	
	public OrganizationBean createOrganization(UserBean me, OrganizationDTO organizationDTO) throws IOException;
	
	public OrganizationBean updateOrganization(
			OrganizationBean organizationBean, 
			OrganizationDTO organizationDTO) throws IOException;
	
	public OrganizationUserMapBean addOrganizationOrdinaryMember(OrganizationBean organization, UserBean user);
	public void removeOrganizationMember(
			Authentication authentication, 
			OrganizationBean organization, 
			Integer organizationUserMapId) throws IOException;
	
	public void promoteOrganizationManager(
			OrganizationBean organization, 
			Integer organizationUserMapId) throws IOException;
	public void demoteOrganizationManager(
			Authentication authentication, 
			OrganizationBean organization, 
			Integer organizatioUserMapId) throws IOException;
	
	public void deleteOrganization(OrganizationBean organization) throws IOException;
}
