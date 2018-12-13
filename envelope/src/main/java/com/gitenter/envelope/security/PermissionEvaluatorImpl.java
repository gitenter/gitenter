package com.gitenter.envelope.security;

import java.io.Serializable;

import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.gitenter.envelope.dto.RepositoryAccessLevel;
import com.gitenter.protease.domain.auth.MemberBean;
import com.gitenter.protease.domain.auth.OrganizationBean;
import com.gitenter.protease.domain.auth.OrganizationMemberRole;
import com.gitenter.protease.domain.auth.RepositoryBean;
import com.gitenter.protease.domain.auth.RepositoryMemberRole;

@Component
public class PermissionEvaluatorImpl implements PermissionEvaluator {

	@Override
	public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
		
		if (targetDomainObject instanceof String) {
			String username = (String)targetDomainObject;
			return username.equals(authentication.getName());
		}
		
		if ((targetDomainObject instanceof OrganizationBean) && (permission instanceof OrganizationMemberRole)) {
			OrganizationBean organization = (OrganizationBean)targetDomainObject;
			OrganizationMemberRole role = (OrganizationMemberRole)permission;
			
			/*
			 * TODO:
			 * Can we go through `OrganizationMemberMap` so we don't need to iterate
			 * through the entire list of members?
			 */
			for (MemberBean member : organization.getMembers(role)) {
				if (member.getUsername().equals(authentication.getName())) {
					return true;
				}
			}
			
			return false;
		}
		
		if ((targetDomainObject instanceof RepositoryBean) && (permission instanceof RepositoryMemberRole)) {
			RepositoryBean repository = (RepositoryBean)targetDomainObject;
			RepositoryMemberRole role = (RepositoryMemberRole)permission;
			
			/*
			 * TODO:
			 * Can we go through `RepositoryMemberMap` so we don't need to iterate
			 * through the entire list of members?
			 */
			for (MemberBean member : repository.getMembers(role)) {
				if (member.getUsername().equals(authentication.getName())) {
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
					for (MemberBean member : repository.getOrganization().getMembers()) {
						if (member.getUsername().equals(authentication.getName())) {
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
