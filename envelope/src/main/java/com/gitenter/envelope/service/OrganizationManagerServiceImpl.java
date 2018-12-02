package com.gitenter.envelope.service;

import java.io.IOException;
import java.util.List;
import java.util.ListIterator;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gitenter.envelope.dto.OrganizationDTO;
import com.gitenter.envelope.service.exception.IdNotExistException;
import com.gitenter.envelope.service.exception.InputIsNotQualifiedException;
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
	public void addOrganizationMember(OrganizationBean organization, String username) {
				
		MemberBean member = memberRepository.findByUsername(username).get(0);
		OrganizationMemberMapBean map = OrganizationMemberMapBean.link(organization, member, OrganizationMemberRole.MEMBER);
		organizationMemberMapRepository.saveAndFlush(map);
	}
	
	@PreAuthorize("hasPermission(#organization, T(com.gitenter.protease.domain.auth.OrganizationMemberRole).MANAGER)")
	@Transactional
	@Override
	public void removeOrganizationMember(OrganizationBean organization, Integer organizationMemberMapId) {
		
		/*
		 * TODO:
		 * Should we validate the `organizationMemberMapId`?
		 */
		
		organizationMemberMapRepository.throughSqlDeleteById(organizationMemberMapId);
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
			throw new InputIsNotQualifiedException("Manager cannot remove him/herself as manager");
		}
		
		map.setRole(OrganizationMemberRole.MEMBER);
		organizationMemberMapRepository.saveAndFlush(map);
	}
}
