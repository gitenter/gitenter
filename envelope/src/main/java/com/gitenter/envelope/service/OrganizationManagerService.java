package com.gitenter.envelope.service;

import java.io.IOException;

import org.springframework.security.core.Authentication;

import com.gitenter.envelope.dto.OrganizationDTO;
import com.gitenter.protease.domain.auth.MemberBean;
import com.gitenter.protease.domain.auth.OrganizationBean;

public interface OrganizationManagerService {
	
	public void updateOrganization(
			Authentication authentication, 
			OrganizationBean organizationBean, 
			OrganizationDTO organizationDTO);
	
	public void addOrganizationMember(OrganizationBean organization, MemberBean member);
	public void removeOrganizationMember(
			OrganizationBean organization, 
			Integer organizationMemberMapId) throws IOException;
	
	public void addOrganizationManager(
			OrganizationBean organization, 
			Integer organizationMemberMapId) throws IOException;
	public void removeOrganizationManager(
			Authentication authentication, 
			OrganizationBean organization, 
			Integer organizationMemberMapId) throws IOException;
}
