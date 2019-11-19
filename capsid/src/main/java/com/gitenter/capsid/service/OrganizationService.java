package com.gitenter.capsid.service;

import java.io.IOException;
import java.util.Collection;

import org.springframework.security.core.Authentication;

import com.gitenter.protease.domain.auth.PersonBean;
import com.gitenter.protease.domain.auth.OrganizationBean;
import com.gitenter.protease.domain.auth.OrganizationPersonMapBean;
import com.gitenter.protease.domain.auth.RepositoryBean;

public interface OrganizationService {

	public OrganizationBean getOrganization(Integer organizationId) throws IOException;

	public Collection<OrganizationPersonMapBean> getManagerMaps(Integer organizationId) throws IOException;
	public Collection<OrganizationPersonMapBean> getMemberMaps(Integer organizationId) throws IOException;
	public Collection<PersonBean> getAllPersons(Integer organizationId) throws IOException;
	public boolean isManager(Integer organizationId, Authentication authentication) throws IOException;
	public boolean isMember(Integer organizationId, Authentication authentication) throws IOException;
	
	public Collection<RepositoryBean> getVisibleRepositories(Integer organizationId, Authentication authentication) throws IOException;
}
