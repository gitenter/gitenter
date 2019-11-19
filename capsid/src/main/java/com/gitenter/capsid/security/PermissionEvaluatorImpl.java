package com.gitenter.capsid.security;

import java.io.Serializable;

import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.gitenter.capsid.dto.PersonProfileDTO;
import com.gitenter.capsid.dto.PersonRegisterDTO;
import com.gitenter.capsid.dto.RepositoryAccessLevel;
import com.gitenter.protease.domain.auth.PersonBean;
import com.gitenter.protease.domain.auth.OrganizationBean;
import com.gitenter.protease.domain.auth.OrganizationPersonRole;
import com.gitenter.protease.domain.auth.RepositoryBean;
import com.gitenter.protease.domain.auth.RepositoryPersonRole;

@Component
public class PermissionEvaluatorImpl implements PermissionEvaluator {

	@Override
	public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
		
		if ((targetDomainObject instanceof String) && (permission.equals(PersonSecurityRole.SELF))) {
			String username = (String)targetDomainObject;
			return username.equals(authentication.getName());
		}
		
		if ((targetDomainObject instanceof PersonBean) && (permission.equals(PersonSecurityRole.SELF))) {
			String username = ((PersonBean)targetDomainObject).getUsername();
			return username.equals(authentication.getName());
		}
		
		if ((targetDomainObject instanceof PersonRegisterDTO) && (permission.equals(PersonSecurityRole.SELF))) {
			String username = ((PersonRegisterDTO)targetDomainObject).getUsername();
			return username.equals(authentication.getName());
		}
		
		if ((targetDomainObject instanceof PersonProfileDTO) && (permission.equals(PersonSecurityRole.SELF))) {
			String username = ((PersonProfileDTO)targetDomainObject).getUsername();
			return username.equals(authentication.getName());
		}
		
		if ((targetDomainObject instanceof OrganizationBean) && (permission instanceof OrganizationPersonRole)) {
			OrganizationBean organization = (OrganizationBean)targetDomainObject;
			OrganizationPersonRole role = (OrganizationPersonRole)permission;
			
			/*
			 * TODO:
			 * Can we go through `OrganizationPersonMap` so we don't need to iterate
			 * through the entire list for members?
			 */
			for (PersonBean person : organization.getPersons(role)) {
				if (person.getUsername().equals(authentication.getName())) {
					return true;
				}
			}
			
			return false;
		}
		
		if ((targetDomainObject instanceof RepositoryBean) && (permission instanceof RepositoryPersonRole)) {
			RepositoryBean repository = (RepositoryBean)targetDomainObject;
			RepositoryPersonRole role = (RepositoryPersonRole)permission;
			
			/*
			 * TODO:
			 * Can we go through `RepositoryMemberMap` so we don't need to iterate
			 * through the entire list of members?
			 */
			for (PersonBean person : repository.getPersons(role)) {
				if (person.getUsername().equals(authentication.getName())) {
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
					for (PersonBean person : repository.getOrganization().getPersons()) {
						if (person.getUsername().equals(authentication.getName())) {
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
