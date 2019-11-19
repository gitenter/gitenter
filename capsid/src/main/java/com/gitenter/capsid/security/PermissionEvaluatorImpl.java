package com.gitenter.capsid.security;

import java.io.Serializable;

import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.gitenter.capsid.dto.UserProfileDTO;
import com.gitenter.capsid.dto.UserRegisterDTO;
import com.gitenter.capsid.dto.RepositoryAccessLevel;
import com.gitenter.protease.domain.auth.UserBean;
import com.gitenter.protease.domain.auth.OrganizationBean;
import com.gitenter.protease.domain.auth.OrganizationUserRole;
import com.gitenter.protease.domain.auth.RepositoryBean;
import com.gitenter.protease.domain.auth.RepositoryUserRole;

@Component
public class PermissionEvaluatorImpl implements PermissionEvaluator {

	@Override
	public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
		
		if ((targetDomainObject instanceof String) && (permission.equals(UserSecurityRole.SELF))) {
			String username = (String)targetDomainObject;
			return username.equals(authentication.getName());
		}
		
		if ((targetDomainObject instanceof UserBean) && (permission.equals(UserSecurityRole.SELF))) {
			String username = ((UserBean)targetDomainObject).getUsername();
			return username.equals(authentication.getName());
		}
		
		if ((targetDomainObject instanceof UserRegisterDTO) && (permission.equals(UserSecurityRole.SELF))) {
			String username = ((UserRegisterDTO)targetDomainObject).getUsername();
			return username.equals(authentication.getName());
		}
		
		if ((targetDomainObject instanceof UserProfileDTO) && (permission.equals(UserSecurityRole.SELF))) {
			String username = ((UserProfileDTO)targetDomainObject).getUsername();
			return username.equals(authentication.getName());
		}
		
		if ((targetDomainObject instanceof OrganizationBean) && (permission instanceof OrganizationUserRole)) {
			OrganizationBean organization = (OrganizationBean)targetDomainObject;
			OrganizationUserRole role = (OrganizationUserRole)permission;
			
			/*
			 * TODO:
			 * Can we go through `OrganizationUserMap` so we don't need to iterate
			 * through the entire list for members?
			 */
			for (UserBean user : organization.getUsers(role)) {
				if (user.getUsername().equals(authentication.getName())) {
					return true;
				}
			}
			
			return false;
		}
		
		if ((targetDomainObject instanceof RepositoryBean) && (permission instanceof RepositoryUserRole)) {
			RepositoryBean repository = (RepositoryBean)targetDomainObject;
			RepositoryUserRole role = (RepositoryUserRole)permission;
			
			/*
			 * TODO:
			 * Can we go through `RepositoryMemberMap` so we don't need to iterate
			 * through the entire list of members?
			 */
			for (UserBean user : repository.getUsers(role)) {
				if (user.getUsername().equals(authentication.getName())) {
					return true;
				}
			}
			
			return false;
		}
		
		if ((targetDomainObject instanceof RepositoryBean) && (permission instanceof RepositoryAccessLevel)) {
			RepositoryBean repository = (RepositoryBean)targetDomainObject;
			RepositoryAccessLevel level = (RepositoryAccessLevel)permission;
			
			/*
			 * TODO:
			 * Move this kind of if-else logic into `RepositoryAccessLevel`.
			 */
			if (repository.getIsPublic().equals(true)) {
				if (level.equals(RepositoryAccessLevel.READ)) {
					return true;
				}
			}
			
			if (repository.getIsPublic().equals(false)) {
				if (level.equals(RepositoryAccessLevel.READ)) {
					for (UserBean user : repository.getOrganization().getUsers()) {
						if (user.getUsername().equals(authentication.getName())) {
							return true;
						}
					}
				}
			}
			
			/*
			 * TODO:
			 * RepositoryAccessLevel.EDITOR
			 */
		}
		
		return false;
	}
	
	@Override
	public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
		throw new UnsupportedOperationException();
	}

}
