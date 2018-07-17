package com.gitenter.envelope.security;

import java.io.Serializable;

import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.gitenter.protease.domain.auth.OrganizationBean;
import com.gitenter.protease.domain.auth.OrganizationMemberRole;

@Component
public class PermissionEvaluatorImpl implements PermissionEvaluator {

	@Override
	public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
		
//		if ((authentication == null) || (targetDomainObject == null) || !(permission instanceof String)) {
//			return false;
//		}
//		
//		if (targetDomainObject instanceof OrganizationBean) {
//			if (OrganizationMemberRole.MANAGER.name().equals(permission)) {
//				return true;
//			}
//			if (OrganizationMemberRole.MEMBER.name().equals(permission)) {
//				return true;
//			}
//		}
		
		return false;
	}

	@Override
	public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
		throw new UnsupportedOperationException();
	}

}
