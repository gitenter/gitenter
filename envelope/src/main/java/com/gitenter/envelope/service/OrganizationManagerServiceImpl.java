package com.gitenter.envelope.service;

import java.util.ListIterator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gitenter.envelope.dto.OrganizationDTO;
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
	public void updateOrganization(Authentication authentication, OrganizationBean organizationBean, OrganizationDTO organizationDTO) {
		
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
	public void removeOrganizationMember(OrganizationBean organization, String username) {
		
		ListIterator<OrganizationMemberMapBean> iter = organization.getOrganizationMemberMaps().listIterator();
		while(iter.hasNext()){
			OrganizationMemberMapBean map = iter.next();
			MemberBean member = map.getMember();
			if(member.getUsername().equals(username)){
				Integer mapId = map.getId();
				System.out.println("MapId: "+mapId);
				organizationMemberMapRepository.throughSqldeleteById(mapId);
				break;
			}
		}
	}
	
	@PreAuthorize("hasPermission(#organization, T(com.gitenter.protease.domain.auth.OrganizationMemberRole).MANAGER)")
	@Override
	public void addOrganizationManager(OrganizationBean organization, String username) {
		
		for (OrganizationMemberMapBean map : organization.getOrganizationMemberMaps()) {
			if (map.getMember().getUsername().equals(username)) {
				/*
				 * So it will only add if the user is already a MEMBER of that organization.
				 */
				if (map.getRole().equals(OrganizationMemberRole.MEMBER)) {
					map.setRole(OrganizationMemberRole.MANAGER);
					organizationMemberMapRepository.saveAndFlush(map);
				}
				
				break;
			}
		}
	}
	
	@PreAuthorize("hasPermission(#organization, T(com.gitenter.protease.domain.auth.OrganizationMemberRole).MANAGER)")
	@Override
	public void removeOrganizationManager(OrganizationBean organization, String username) {
	
		for (OrganizationMemberMapBean map : organization.getOrganizationMemberMaps()) {
			if (map.getMember().getUsername().equals(username)) {
				/*
				 * The code in here doesn't check that the user cannot remove himself/herself
				 * as a manager (although the UI doesn't provide the link). This is the case
				 * because this is a general method. That constrain will be implemented in the
				 * controller level.
				 */
				if (map.getRole().equals(OrganizationMemberRole.MANAGER)) {
					map.setRole(OrganizationMemberRole.MEMBER);
					organizationMemberMapRepository.saveAndFlush(map);
				}
				
				break;
			}
		}
	}
}
