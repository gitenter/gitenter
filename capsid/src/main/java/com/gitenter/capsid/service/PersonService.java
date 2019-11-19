package com.gitenter.capsid.service;

import java.io.IOException;
import java.util.Collection;

import org.springframework.security.core.Authentication;

import com.gitenter.capsid.dto.PersonProfileDTO;
import com.gitenter.capsid.dto.PersonRegisterDTO;
import com.gitenter.protease.domain.auth.PersonBean;
import com.gitenter.protease.domain.auth.OrganizationBean;
import com.gitenter.protease.domain.auth.RepositoryBean;
import com.gitenter.protease.domain.auth.SshKeyBean;

public interface PersonService {
	
	/*
	 * These methods using input "username" rather than "authentication",
	 * because when some OTHER user are visiting the profile of this user,
	 * they also need these services.
	 * 
	 * We may consider moving these ones to "AnonymousService", but as far
	 * as we cannot annotate @PreAuthorize in the class scope (rather than
	 * in the method scope), there's not a lot of benefits doing that.
	 */
	public PersonBean getPersonByUsername(String username) throws IOException;
	public PersonBean getMe(Authentication authentication) throws IOException;
	
	/*
	 * Basically to have the input of "Authentication", it has similar the same
	 * effect as @PreAuthorize("isAuthenticated()").
	 */
	public PersonProfileDTO getPersonProfileDTO(Authentication authentication) throws IOException;
	public PersonRegisterDTO getPersonRegisterDTO(Authentication authentication) throws IOException;
	public void updatePerson(PersonProfileDTO profile) throws IOException;
	public boolean updatePassword(PersonRegisterDTO register, String oldPassword) throws IOException;
	
	public Collection<OrganizationBean> getManagedOrganizations(String username) throws IOException;
	public Collection<OrganizationBean> getBelongedOrganizations(String username) throws IOException;
	
	public Collection<RepositoryBean> getOrganizedRepositories(String username) throws IOException;
	public Collection<RepositoryBean> getAuthoredRepositories(String username) throws IOException;
	
	public void addSshKey(SshKeyBean sshKey, PersonBean person) throws IOException;
	public boolean deletePerson(String username, String password) throws IOException;
}
