package com.gitenter.envelope.service;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.gitenter.protease.dao.auth.OrganizationRepository;
import com.gitenter.protease.domain.auth.MemberBean;
import com.gitenter.protease.domain.auth.OrganizationBean;
import com.gitenter.protease.domain.auth.OrganizationMemberRole;
import com.gitenter.protease.domain.auth.RepositoryBean;

@Service
public class OrganizationServiceImpl implements OrganizationService {
	
	@Autowired OrganizationRepository organizationRepository;

	@Override
	public OrganizationBean getOrganization(Integer organizationId) {
		return organizationRepository.findById(organizationId).get();
	}
	
	@Override
	public Collection<MemberBean> getManagers(Integer organizationId) {
		
		OrganizationBean organization = getOrganization(organizationId);
		return organization.getMembers(OrganizationMemberRole.MANAGER);
	}
	
	@Override
	public boolean isManagedBy(Integer organizationId, Authentication authentication) {
		
		for (MemberBean manager : getManagers(organizationId)) {
			if (manager.getUsername().equals(authentication.getName())) {
				return true;
			}
		}
		return false;
	}

	@Override
	public Collection<RepositoryBean> getVisibleRepositories(Integer organizationId, Authentication authentication) {
		
		/*
		 * TODO:
		 * 
		 * Show all repositories if the user belongs to that organization.
		 * 
		 * Show only public repositories if the user doesn't belong to 
		 * the organization.
		 */
		OrganizationBean organization = getOrganization(organizationId);
		return organization.getRepositories();
	}
}
