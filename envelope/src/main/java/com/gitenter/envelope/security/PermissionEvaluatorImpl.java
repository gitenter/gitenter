package com.gitenter.envelope.security;

import java.io.Serializable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.gitenter.protease.dao.auth.OrganizationRepository;
import com.gitenter.protease.domain.auth.MemberBean;
import com.gitenter.protease.domain.auth.OrganizationBean;
import com.gitenter.protease.domain.auth.OrganizationMemberRole;

@Component
public class PermissionEvaluatorImpl implements PermissionEvaluator {

	@Autowired OrganizationRepository organizationRepository;
	
	@Override
	public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
		
		/*
		 * Since "targetDomainObject" is mostly just an integer (ID), here is secretly using
		 * "permission" type to distinguish whether it is a per-organization or per-repository
		 * authorization.
		 * 
		 * A better way is probably to have "targetDomainObject" to be either "OrganizationBean"
		 * or "RepositoryBean", and don't need this method to involve @Autowired repositories.
		 * However, since by-method "@PreAuthorize" always has input of plain ID (we may have 
		 * private helper method to input beans, but it seems not working with this annotation)
		 * that doesn't work.
		 */
		if (permission instanceof OrganizationMemberRole) {
			
			Integer organizationId = (Integer)targetDomainObject;
			OrganizationBean organization = organizationRepository.findById(organizationId).get();
			
			OrganizationMemberRole role = (OrganizationMemberRole)permission;
			
			for (MemberBean member : organization.getMembers(role)) {
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
