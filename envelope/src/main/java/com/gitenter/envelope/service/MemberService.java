package com.gitenter.envelope.service;

import java.util.Collection;

import com.gitenter.protease.domain.auth.OrganizationBean;
import com.gitenter.protease.domain.auth.RepositoryBean;

public interface MemberService {
	
	public Collection<OrganizationBean> getManagedOrganizations();
	public Collection<OrganizationBean> getAccessibleOrganizations();
	
	public Collection<RepositoryBean> getOrganizedRepositories();
	public Collection<RepositoryBean> getEditableRepositories();
}
