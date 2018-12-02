package com.gitenter.envelope.service;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.gitenter.envelope.service.exception.InvalidDataException;
import com.gitenter.envelope.service.exception.IdNotExistException;
import com.gitenter.protease.dao.auth.OrganizationMemberMapRepository;
import com.gitenter.protease.dao.auth.OrganizationRepository;
import com.gitenter.protease.domain.auth.MemberBean;
import com.gitenter.protease.domain.auth.OrganizationBean;
import com.gitenter.protease.domain.auth.OrganizationMemberMapBean;
import com.gitenter.protease.domain.auth.OrganizationMemberRole;
import com.gitenter.protease.domain.auth.RepositoryBean;

@Service
public class OrganizationServiceImpl implements OrganizationService {
	
	@Autowired OrganizationRepository organizationRepository;
	@Autowired OrganizationMemberMapRepository organizationMemberMapRepository;

	@Override
	public OrganizationBean getOrganization(Integer organizationId) throws IOException {
		
		Optional<OrganizationBean> organizations = organizationRepository.findById(organizationId);
		
		if (organizations.isPresent()) {
			return organizations.get();
		}
		else {
			throw new IdNotExistException(OrganizationBean.class, organizationId);
		}
	}
	
	@Override
	public Collection<OrganizationMemberMapBean> getManagerMaps(Integer organizationId) throws IOException {
		
		OrganizationBean organization = getOrganization(organizationId);
		return organization.getMemberMaps(OrganizationMemberRole.MANAGER);
	}
	
	@Override
	public Collection<OrganizationMemberMapBean> getOrdinaryMemberMaps(Integer organizationId) throws IOException {
		
		OrganizationBean organization = getOrganization(organizationId);
		return organization.getMemberMaps(OrganizationMemberRole.MEMBER);
	}
	
	@Override
	public Collection<MemberBean> getAllMembers(Integer organizationId) throws IOException {
		
		OrganizationBean organization = getOrganization(organizationId);
		return organization.getMembers();
	}
	
	@Override
	public boolean isManager(Integer organizationId, Authentication authentication) throws IOException {
		
		List<OrganizationMemberMapBean> maps = organizationMemberMapRepository.findByUsernameAndOrganizationIdAndRole(
				authentication.getName(), organizationId, OrganizationMemberRole.MANAGER);
		
		if (maps.size() == 1) {
			return true;
		}
		
		if (maps.size() == 0) {
			return false;
		}
		
		throw new InvalidDataException("user should have a unique relationship to an organization.");
	}

	@Override
	public boolean isMember(Integer organizationId, Authentication authentication) throws IOException {
		
		List<OrganizationMemberMapBean> maps = organizationMemberMapRepository.findByUsernameAndOrganizationIdAndRole(
				authentication.getName(), organizationId, OrganizationMemberRole.MEMBER);
		
		if (maps.size() == 1) {
			return true;
		}
		
		if (maps.size() == 0) {
			return false;
		}
		
		throw new InvalidDataException("user should have a unique relationship to an organization.");
	}

	@Override
	public Collection<RepositoryBean> getVisibleRepositories(Integer organizationId, Authentication authentication) throws IOException {
		
		OrganizationBean organization = getOrganization(organizationId);
		if (isMember(organizationId, authentication)) {
			return organization.getRepositories();
		}
		else {
			return organization.getRepositories(true);
		}
	}
}
