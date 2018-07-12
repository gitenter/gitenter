package com.gitenter.envelope.service;

import java.util.Collection;

import org.springframework.security.core.Authentication;

import com.gitenter.protease.domain.auth.MemberBean;
import com.gitenter.protease.domain.auth.OrganizationBean;
import com.gitenter.protease.domain.auth.RepositoryBean;

public interface OrganizationService {

	public OrganizationBean getOrganization(Integer organizationId);

	public Collection<MemberBean> getManagers(Integer organizationId);
	public boolean isManagedBy(Integer organizationId, Authentication authentication);
	
	public Collection<RepositoryBean> getVisibleRepositories(Integer organizationId, Authentication authentication);
}
