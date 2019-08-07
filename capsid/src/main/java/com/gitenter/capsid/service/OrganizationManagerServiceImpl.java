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
import com.gitenter.capsid.service.exception.UnreachableOperationException;
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
	
	@Override
	@PreAuthorize("hasPermission(#organizationBean, T(com.gitenter.protease.domain.auth.OrganizationMemberRole).MANAGER)")
	public void updateOrganization(
			Authentication authentication, 
			OrganizationBean organizationBean, 
			OrganizationDTO organizationDTO) throws IOException {
		
		if (!organizationBean.getName().equals(organizationDTO.getName())) {
			throw new UnreachableOperationException("POST Request is generated from unexpected source. "
					+ "OrganizationDTO should have organization name "+organizationBean.getName()
					+ ", but it is actually "+organizationDTO.getName());
		}
		
		organizationDTO.updateBean(organizationBean);
		organizationRepository.saveAndFlush(organizationBean);
	}
	
	@Override
	@PreAuthorize("hasPermission(#organization, T(com.gitenter.protease.domain.auth.OrganizationMemberRole).MANAGER)")
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
	
	@Override
	@Transactional
	@PreAuthorize("hasPermission(#organization, T(com.gitenter.protease.domain.auth.OrganizationMemberRole).MANAGER)")
	public void removeOrganizationMember(OrganizationBean organization, Integer organizationMemberMapId) throws IOException {
		
		OrganizationMemberMapBean map = getOrganizationMemberMapBean(organizationMemberMapId);
		
		/*
		 * Doesn't for the SQL operation part, since if the `organizationMemberMapId` does not
		 * exist then `DELECT` simply does nothing. The problem is the `@PreAuthorize` is only
		 * for the operator has authorization for the current organization, but have no
		 * requirement if the `mapId` belongs to a completely different organization. That's
		 * the reason this checking is important.
		 */
		if (!map.getOrganization().getId().equals(organization.getId())) {
			throw new UnreachableOperationException("Remove organization member input not consistency. "
					+ "organizationMemberMapId "+organizationMemberMapId+" doesn't belong to the "
					+ "target organization "+organization);
		}
		
		organizationMemberMapRepository.throughSqlDeleteById(organizationMemberMapId);
	}
	
	@Override
	@PreAuthorize("hasPermission(#organization, T(com.gitenter.protease.domain.auth.OrganizationMemberRole).MANAGER)")
	public void addOrganizationManager(OrganizationBean organization, Integer organizationMemberMapId) throws IOException {
		
		OrganizationMemberMapBean map = getOrganizationMemberMapBean(organizationMemberMapId);
		
		if (!map.getOrganization().getId().equals(organization.getId())) {
			throw new UnreachableOperationException("Add organization member input not consistency. "
					+ "organizationMemberMapId "+organizationMemberMapId+" doesn't belong to the "
					+ "target organization "+organization);
		}
		
		if (map.getRole().equals(OrganizationMemberRole.MANAGER)) {
			throw new UnreachableOperationException("User is already a manager of the target organization.");
		}
		
		map.setRole(OrganizationMemberRole.MANAGER);
		organizationMemberMapRepository.saveAndFlush(map);
	}
	
	@Override
	@PreAuthorize("hasPermission(#organization, T(com.gitenter.protease.domain.auth.OrganizationMemberRole).MANAGER)")
	public void removeOrganizationManager(
			Authentication authentication,
			OrganizationBean organization, 
			Integer organizationMemberMapId) throws IOException {
		
		OrganizationMemberMapBean map = getOrganizationMemberMapBean(organizationMemberMapId);
		
		if (!map.getOrganization().getId().equals(organization.getId())) {
			throw new UnreachableOperationException("Remove organization member input not consistency. "
					+ "organizationMemberMapId "+organizationMemberMapId+" doesn't belong to the"
					+ " target organization "+organization);
		}
		
		if (!map.getRole().equals(OrganizationMemberRole.MANAGER)) {
			throw new UnreachableOperationException("User is currently not a manager of the target organization. Current role "+map.getRole());
		}
		
		if (authentication.getName().equals(map.getMember().getUsername())) {
			throw new InvalidOperationException("Rejected "+authentication.getName()+" to remove him/herself as a manager of organization "+organization);
		}
		
		map.setRole(OrganizationMemberRole.MEMBER);
		organizationMemberMapRepository.saveAndFlush(map);
	}
	
	@Override
	@PreAuthorize("hasPermission(#organization, T(com.gitenter.protease.domain.auth.OrganizationMemberRole).MANAGER)")
	public void deleteOrganization(OrganizationBean organization) throws IOException {
		
		/*
		 * TODO:
		 * Audit log.
		 */
		organizationRepository.delete(organization);
	}
}
