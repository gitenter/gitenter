package com.gitenter.envelope.security;

import java.io.Serializable;

import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.gitenter.protease.domain.auth.MemberBean;
import com.gitenter.protease.domain.auth.OrganizationBean;
import com.gitenter.protease.domain.auth.OrganizationMemberRole;
import com.gitenter.protease.domain.auth.RepositoryBean;
import com.gitenter.protease.domain.auth.RepositoryMemberRole;

@Component
public class PermissionEvaluatorImpl implements PermissionEvaluator {

	@Override
	public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
		
		if ((targetDomainObject instanceof OrganizationBean) && (permission instanceof OrganizationMemberRole)) {
			
			OrganizationBean organization = (OrganizationBean)targetDomainObject;
			OrganizationMemberRole role = (OrganizationMemberRole)permission;
			
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
			
			for (MemberBean member : repository.getMembers(role)) {
				if (member.getUsername().equals(authentication.getName())) {
					return true;
				}
			}
			
			return false;
		}
		
		if (permission instanceof String) {
			if (permission.equals("MANAGER")) {
				return true;
			}
		}
		
		return false;
	}
	
	@Override
	public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
		throw new UnsupportedOperationException();
	}

}
