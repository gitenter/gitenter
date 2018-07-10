package com.gitenter.envelope.service;

import java.util.Collection;

import com.gitenter.protease.domain.auth.OrganizationBean;
import com.gitenter.protease.domain.auth.RepositoryBean;

public interface MemberService {
	
	public Collection<OrganizationBean> getManagedOrganizations(String username);
	public Collection<OrganizationBean> getAccessibleOrganizations(String username);
	
	public Collection<RepositoryBean> getOrganizedRepositories(String username);
	public Collection<RepositoryBean> getEditableRepositories(String username);
}
