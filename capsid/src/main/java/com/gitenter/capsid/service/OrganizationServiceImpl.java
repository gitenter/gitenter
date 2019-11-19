package com.gitenter.capsid.service;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.gitenter.capsid.service.exception.IdNotExistException;
import com.gitenter.capsid.service.exception.InvalidDataStateException;
import com.gitenter.protease.dao.auth.OrganizationPersonMapRepository;
import com.gitenter.protease.dao.auth.OrganizationRepository;
import com.gitenter.protease.domain.auth.PersonBean;
import com.gitenter.protease.domain.auth.OrganizationBean;
import com.gitenter.protease.domain.auth.OrganizationPersonMapBean;
import com.gitenter.protease.domain.auth.OrganizationPersonRole;
import com.gitenter.protease.domain.auth.RepositoryBean;

@Service
public class OrganizationServiceImpl implements OrganizationService {
	
	@Autowired OrganizationRepository organizationRepository;
	@Autowired OrganizationPersonMapRepository organizationPersonMapRepository;

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
	public Collection<OrganizationPersonMapBean> getManagerMaps(Integer organizationId) throws IOException {
		
		OrganizationBean organization = getOrganization(organizationId);
		return organization.getPersonMaps(OrganizationPersonRole.MANAGER);
	}
	
	@Override
	public Collection<OrganizationPersonMapBean> getMemberMaps(Integer organizationId) throws IOException {
		
		OrganizationBean organization = getOrganization(organizationId);
		return organization.getPersonMaps(OrganizationPersonRole.MEMBER);
	}
	
	@Override
	public Collection<PersonBean> getAllPersons(Integer organizationId) throws IOException {
		
		OrganizationBean organization = getOrganization(organizationId);
		return organization.getPersons();
	}
	
	@Override
	@PreAuthorize("isAuthenticated()")
	public boolean isManager(Integer organizationId, Authentication authentication) throws IOException {
		
		List<OrganizationPersonMapBean> maps = organizationPersonMapRepository.findByUsernameAndOrganizationIdAndRole(
				authentication.getName(), organizationId, OrganizationPersonRole.MANAGER);
		
		if (maps.size() == 1) {
			return true;
		}
		
		if (maps.size() == 0) {
			return false;
		}
		
		throw new InvalidDataStateException("user should have a unique relationship to an organization.");
	}

	@Override
	@PreAuthorize("isAuthenticated()")
	public boolean isMember(Integer organizationId, Authentication authentication) throws IOException {
		
		List<OrganizationPersonMapBean> maps = organizationPersonMapRepository.findByUsernameAndOrganizationIdAndRole(
				authentication.getName(), organizationId, OrganizationPersonRole.MEMBER);
		
		if (maps.size() == 1) {
			return true;
		}
		
		if (maps.size() == 0) {
			return false;
		}
		
		throw new InvalidDataStateException("user should have a unique relationship to an organization.");
	}

	/*
	 * TODO:
	 * 
	 * Also need to handle the case of `getVisibleRepositories()` for
	 * an anonymous user. For this case, `authentication` cannot be
	 * the argument of this method.
	 */
	@Override
	@PreAuthorize("isAuthenticated()")
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
