package com.gitenter.envelope.service;

import java.io.IOException;
import java.util.Collection;

import org.springframework.security.core.Authentication;

import com.gitenter.protease.domain.auth.MemberBean;
import com.gitenter.protease.domain.auth.OrganizationBean;
import com.gitenter.protease.domain.auth.RepositoryBean;

public interface OrganizationService {

	public OrganizationBean getOrganization(Integer organizationId) throws IOException;

	public Collection<MemberBean> getManagers(Integer organizationId) throws IOException;
	public Collection<MemberBean> getOrdinaryMembers(Integer organizationId) throws IOException;
	public Collection<MemberBean> getAllMembers(Integer organizationId) throws IOException;
	public boolean isManager(Integer organizationId, Authentication authentication) throws IOException;
	public boolean isMember(Integer organizationId, Authentication authentication) throws IOException;
	
	public Collection<RepositoryBean> getVisibleRepositories(Integer organizationId, Authentication authentication) throws IOException;
}
