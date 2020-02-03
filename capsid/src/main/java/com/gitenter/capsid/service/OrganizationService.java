package com.gitenter.capsid.service;

import java.io.IOException;
import java.util.List;

import org.springframework.security.core.Authentication;

import com.gitenter.protease.domain.auth.OrganizationBean;
import com.gitenter.protease.domain.auth.OrganizationUserMapBean;
import com.gitenter.protease.domain.auth.RepositoryBean;
import com.gitenter.protease.domain.auth.UserBean;

public interface OrganizationService {

	public OrganizationBean getOrganization(Integer organizationId) throws IOException;

	public List<OrganizationUserMapBean> getManagerMaps(OrganizationBean organization);
	public List<OrganizationUserMapBean> getOrdinaryMemberMaps(OrganizationBean organization);
	public List<OrganizationUserMapBean> getAllMemberMaps(OrganizationBean organization);
	public List<UserBean> getAllManagers(OrganizationBean organization);
	public List<UserBean> getAllOrdinaryMembers(OrganizationBean organization);
	public List<UserBean> getAllMembers(OrganizationBean organization);
	public boolean isManager(Integer organizationId, Authentication authentication) throws IOException;
	public boolean isMember(Integer organizationId, Authentication authentication) throws IOException;
	
	public List<RepositoryBean> getVisibleRepositories(Integer organizationId, Authentication authentication) throws IOException;
}
