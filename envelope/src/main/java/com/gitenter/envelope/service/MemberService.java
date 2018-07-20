package com.gitenter.envelope.service;

import java.util.Collection;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;

import com.gitenter.envelope.dto.MemberRegisterDTO;
import com.gitenter.envelope.dto.MemberProfileDTO;
import com.gitenter.envelope.dto.OrganizationDTO;
import com.gitenter.protease.domain.auth.MemberBean;
import com.gitenter.protease.domain.auth.OrganizationBean;
import com.gitenter.protease.domain.auth.RepositoryBean;

public interface MemberService {
	
	/*
	 * These methods using input "username" rather than "authentication",
	 * because when some OTHER user are visiting the profile of this user,
	 * they also need these services.
	 * 
	 * We may consider moving these ones to "AnonymousService", but as far
	 * as we cannot annotate @PreAuthorize in the class scope (rather than
	 * in the method scope), there's not a lot of benefits doing that.
	 */
	public MemberBean getMemberByUsername(String username);
	
	public Collection<OrganizationBean> getManagedOrganizations(String username);
	public Collection<OrganizationBean> getBelongedOrganizations(String username);
	
	public Collection<RepositoryBean> getOrganizedRepositories(String username);
	public Collection<RepositoryBean> getAuthoredRepositories(String username);
	
	/*
	 * Basically to have the input of "Authentication", it has similar the same
	 * effect as @PreAuthorize("isAuthenticated()").
	 */
	public MemberProfileDTO getMemberProfileDTO(Authentication authentication);
	public MemberRegisterDTO getMemberRegisterDTO(Authentication authentication);
	public void updateMember(MemberProfileDTO profile);
	public boolean updatePassword(MemberRegisterDTO register, String oldPassword);
	
	public void createOrganization(Authentication authentication, OrganizationDTO organizationDTO);
}
