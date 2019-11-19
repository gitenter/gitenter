package com.gitenter.capsid.service;

import java.io.IOException;

import org.springframework.security.core.Authentication;

import com.gitenter.capsid.dto.OrganizationDTO;
import com.gitenter.protease.domain.auth.PersonBean;
import com.gitenter.protease.domain.auth.OrganizationBean;

public interface OrganizationManagerService {
	
	public void createOrganization(PersonBean me, OrganizationDTO organizationDTO) throws IOException;
	
	public void updateOrganization(
			Authentication authentication, 
			OrganizationBean organizationBean, 
			OrganizationDTO organizationDTO) throws IOException;
	
	public void addOrganizationMember(OrganizationBean organization, PersonBean person);
	public void removeOrganizationMember(
			OrganizationBean organization, 
			Integer organizationPersonMapId) throws IOException;
	
	public void addOrganizationManager(
			OrganizationBean organization, 
			Integer organizationPersonMapId) throws IOException;
	public void removeOrganizationManager(
			Authentication authentication, 
			OrganizationBean organization, 
			Integer organizatioPersonMapId) throws IOException;
	
	public void deleteOrganization(OrganizationBean organization) throws IOException;
}
