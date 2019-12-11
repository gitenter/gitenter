package com.gitenter.capsid.service;

import java.io.IOException;
import java.util.List;

import org.springframework.security.core.Authentication;

import com.gitenter.capsid.dto.UserProfileDTO;
import com.gitenter.capsid.dto.UserRegisterDTO;
import com.gitenter.capsid.service.exception.UserNotExistException;
import com.gitenter.protease.domain.auth.OrganizationBean;
import com.gitenter.protease.domain.auth.RepositoryBean;
import com.gitenter.protease.domain.auth.SshKeyBean;
import com.gitenter.protease.domain.auth.UserBean;

public interface UserService {
	
	/*
	 * These methods using input "username" rather than "authentication",
	 * because when some OTHER user are visiting the profile of this user,
	 * they also need these services.
	 * 
	 * We may consider moving these ones to "AnonymousService", but as far
	 * as we cannot annotate @PreAuthorize in the class scope (rather than
	 * in the method scope), there's not a lot of benefits doing that.
	 */
	public UserBean getUserById(Integer memberId) throws UserNotExistException;
	public UserBean getUserByUsername(String username) throws UserNotExistException;
	public UserBean getMe(Authentication authentication) throws UserNotExistException;
	
	/*
	 * Basically to have the input of "Authentication", it has similar the same
	 * effect as @PreAuthorize("isAuthenticated()").
	 */
	public UserProfileDTO getUserProfileDTO(Authentication authentication) throws IOException;
	public UserRegisterDTO getUserRegisterDTO(Authentication authentication) throws IOException;
	public void updateUser(UserProfileDTO profile) throws IOException;
	public boolean updatePassword(UserRegisterDTO register, String oldPassword) throws IOException;
	
	public List<OrganizationBean> getManagedOrganizations(String username) throws IOException;
	public List<OrganizationBean> getBelongedOrganizations(String username) throws IOException;
	
	public List<RepositoryBean> getOrganizedRepositories(String username) throws IOException;
	public List<RepositoryBean> getAuthoredRepositories(String username) throws IOException;
	
	public void addSshKey(SshKeyBean sshKey, UserBean user) throws IOException;
	public boolean deleteUser(String username, String password) throws IOException;
}
