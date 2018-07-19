package com.gitenter.envelope.service;

import java.util.Collection;

import com.gitenter.envelope.dto.OrganizationDTO;
import com.gitenter.protease.domain.auth.MemberBean;
import com.gitenter.protease.domain.auth.OrganizationBean;
import com.gitenter.protease.domain.auth.RepositoryBean;

public interface MemberService {
	
	public MemberBean getMember(String username);
	
	public Collection<OrganizationBean> getManagedOrganizations(String username);
	public Collection<OrganizationBean> getBelongedOrganizations(String username);
	
	public Collection<RepositoryBean> getOrganizedRepositories(String username);
	public Collection<RepositoryBean> getAuthoredRepositories(String username);
	
	public void createOrganization(String username, OrganizationDTO organizationDTO);
}
