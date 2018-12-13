package com.gitenter.capsid.service;

import java.io.IOException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gitenter.capsid.dto.OrganizationDTO;
import com.gitenter.capsid.service.exception.IdNotExistException;
import com.gitenter.capsid.service.exception.InvalidOperationException;
import com.gitenter.protease.dao.auth.MemberRepository;
import com.gitenter.protease.dao.auth.OrganizationMemberMapRepository;
import com.gitenter.protease.dao.auth.OrganizationRepository;
import com.gitenter.protease.domain.auth.MemberBean;
import com.gitenter.protease.domain.auth.OrganizationBean;
import com.gitenter.protease.domain.auth.OrganizationMemberMapBean;
import com.gitenter.protease.domain.auth.OrganizationMemberRole;

@Service
public class OrganizationManagerServiceImpl implements OrganizationManagerService {
	
	@Autowired MemberRepository memberRepository;
	@Autowired OrganizationRepository organizationRepository;
	@Autowired OrganizationMemberMapRepository organizationMemberMapRepository;
	
	@PreAuthorize("hasPermission(#organizationBean, T(com.gitenter.protease.domain.auth.OrganizationMemberRole).MANAGER)")
	@Override
	public void updateOrganization(
			Authentication authentication, 
			OrganizationBean organizationBean, 
			OrganizationDTO organizationDTO) {
		
		organizationDTO.updateBean(organizationBean);
		organizationRepository.saveAndFlush(organizationBean);
	}
	
	@PreAuthorize("hasPermission(#organization, T(com.gitenter.protease.domain.auth.OrganizationMemberRole).MANAGER)")
	@Override
	public void addOrganizationMember(OrganizationBean organization, MemberBean member) {

		OrganizationMemberMapBean map = OrganizationMemberMapBean.link(organization, member, OrganizationMemberRole.MEMBER);
		organizationMemberMapRepository.saveAndFlush(map);
	}
	
	private OrganizationMemberMapBean getOrganizationMemberMapBean(Integer organizationMemberMapId) throws IOException {
		
		Optional<OrganizationMemberMapBean> maps = organizationMemberMapRepository.findById(organizationMemberMapId);
		
		if (maps.isPresent()) {
			return maps.get();
		}
		else {
			throw new IdNotExistException(OrganizationMemberMapBean.class, organizationMemberMapId);
		}
	}
	
	@PreAuthorize("hasPermission(#organization, T(com.gitenter.protease.domain.auth.OrganizationMemberRole).MANAGER)")
	@Transactional
	@Override
	public void removeOrganizationMember(OrganizationBean organization, Integer organizationMemberMapId) throws IOException {
		
		/*
		 * Doesn't for the SQL operation part, since if the `organizationMemberMapId` does not
		 * exist then `DELECT` simply does nothing. The problem is the `@PreAuthorize` is only
		 * for the operator has authorization for the current organization, but have no
		 * requirement if the `mapId` belongs to a completely different organization. That's
		 * the reason this checking is important.
		 */
		OrganizationMemberMapBean map = getOrganizationMemberMapBean(organizationMemberMapId);
		assert map.getOrganization().getId().equals(organization.getId());
		
		organizationMemberMapRepository.throughSqlDeleteById(organizationMemberMapId);
	}
	
	@PreAuthorize("hasPermission(#organization, T(com.gitenter.protease.domain.auth.OrganizationMemberRole).MANAGER)")
	@Override
	public void addOrganizationManager(OrganizationBean organization, Integer organizationMemberMapId) throws IOException {
		
		OrganizationMemberMapBean map = getOrganizationMemberMapBean(organizationMemberMapId);
		assert map.getRole().equals(OrganizationMemberRole.MEMBER);
		
		map.setRole(OrganizationMemberRole.MANAGER);
		organizationMemberMapRepository.saveAndFlush(map);
	}
	
	@PreAuthorize("hasPermission(#organization, T(com.gitenter.protease.domain.auth.OrganizationMemberRole).MANAGER)")
	@Override
	public void removeOrganizationManager(
			Authentication authentication,
			OrganizationBean organization, 
			Integer organizationMemberMapId) throws IOException {
		
		OrganizationMemberMapBean map = getOrganizationMemberMapBean(organizationMemberMapId);
		assert map.getRole().equals(OrganizationMemberRole.MANAGER);
		
		if (authentication.getName().equals(map.getMember().getUsername())) {
			throw new InvalidOperationException("Manager cannot remove him/herself as manager");
		}
		
		map.setRole(OrganizationMemberRole.MEMBER);
		organizationMemberMapRepository.saveAndFlush(map);
	}
}
