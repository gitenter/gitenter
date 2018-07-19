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
	public Collection<MemberBean> getOrdinaryMembers(Integer organizationId) {
		
		OrganizationBean organization = getOrganization(organizationId);
		return organization.getMembers(OrganizationMemberRole.MEMBER);
	}
	
	@Override
	public Collection<MemberBean> getAllMembers(Integer organizationId) {
		
		OrganizationBean organization = getOrganization(organizationId);
		return organization.getMembers();
	}
	
	@Override
	public boolean isManager(Integer organizationId, Authentication authentication) {
		
		for (MemberBean manager : getManagers(organizationId)) {
			if (manager.getUsername().equals(authentication.getName())) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean isMember(Integer organizationId, Authentication authentication) {
		
		for (MemberBean member : getAllMembers(organizationId)) {
			if (member.getUsername().equals(authentication.getName())) {
				return true;
			}
		}
		return false;
	}

	@Override
	public Collection<RepositoryBean> getVisibleRepositories(Integer organizationId, Authentication authentication) {
		
		OrganizationBean organization = getOrganization(organizationId);
		if (isMember(organizationId, authentication)) {
			return organization.getRepositories();
		}
		else {
			return organization.getRepositories(true);
		}
	}
}
