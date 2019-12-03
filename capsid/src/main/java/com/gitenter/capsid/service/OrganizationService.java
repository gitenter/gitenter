package com.gitenter.capsid.service;

import java.io.IOException;
import java.util.Collection;

import org.springframework.security.core.Authentication;

import com.gitenter.protease.domain.auth.UserBean;
import com.gitenter.protease.domain.auth.OrganizationBean;
import com.gitenter.protease.domain.auth.OrganizationUserMapBean;
import com.gitenter.protease.domain.auth.RepositoryBean;

public interface OrganizationService {

	public OrganizationBean getOrganization(Integer organizationId) throws IOException;

	public Collection<OrganizationUserMapBean> getManagerMaps(OrganizationBean organization) throws IOException;
	public Collection<OrganizationUserMapBean> getOrdinaryMemberMaps(OrganizationBean organization) throws IOException;
	public Collection<UserBean> getAllMembers(OrganizationBean organization) throws IOException;
	public boolean isManager(Integer organizationId, Authentication authentication) throws IOException;
	public boolean isMember(Integer organizationId, Authentication authentication) throws IOException;
	
	public Collection<RepositoryBean> getVisibleRepositories(Integer organizationId, Authentication authentication) throws IOException;
}
