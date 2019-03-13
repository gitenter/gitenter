package com.gitenter.capsid.service;

import java.io.IOException;
import java.util.Collection;

import org.springframework.security.core.Authentication;

import com.gitenter.capsid.dto.MemberProfileDTO;
import com.gitenter.capsid.dto.MemberRegisterDTO;
import com.gitenter.capsid.dto.OrganizationDTO;
import com.gitenter.protease.domain.auth.MemberBean;
import com.gitenter.protease.domain.auth.OrganizationBean;
import com.gitenter.protease.domain.auth.RepositoryBean;
import com.gitenter.protease.domain.auth.SshKeyBean;

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
	public MemberBean getMemberByUsername(String username) throws IOException;
	
	/*
	 * Basically to have the input of "Authentication", it has similar the same
	 * effect as @PreAuthorize("isAuthenticated()").
	 */
	public MemberProfileDTO getMemberProfileDTO(Authentication authentication) throws IOException;
	public MemberRegisterDTO getMemberRegisterDTO(Authentication authentication) throws IOException;
	public void updateMember(MemberProfileDTO profile) throws IOException;
	public boolean updatePassword(MemberRegisterDTO register, String oldPassword) throws IOException;
	
	public void createOrganization(Authentication authentication, OrganizationDTO organizationDTO) throws IOException;
	
	public Collection<OrganizationBean> getManagedOrganizations(String username) throws IOException;
	public Collection<OrganizationBean> getBelongedOrganizations(String username) throws IOException;
	
	public Collection<RepositoryBean> getOrganizedRepositories(String username) throws IOException;
	public Collection<RepositoryBean> getAuthoredRepositories(String username) throws IOException;
	
	public void addSshKey(SshKeyBean sshKey, MemberBean member) throws IOException;
}