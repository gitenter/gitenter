package com.gitenter.envelope.service;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gitenter.envelope.dto.OrganizationDTO;
import com.gitenter.protease.dao.auth.MemberRepository;
import com.gitenter.protease.dao.auth.OrganizationMemberMapRepository;
import com.gitenter.protease.dao.auth.OrganizationRepository;
import com.gitenter.protease.domain.auth.MemberBean;
import com.gitenter.protease.domain.auth.OrganizationBean;
import com.gitenter.protease.domain.auth.OrganizationMemberMapBean;
import com.gitenter.protease.domain.auth.OrganizationMemberRole;
import com.gitenter.protease.domain.auth.RepositoryBean;
import com.gitenter.protease.domain.auth.RepositoryMemberRole;

/*
 * It is quite ironical that Spring @autowired are contradict with
 * object-oriented programming. Say a more OO approach is we have
 * a domain class "Member" which:
 * (1) hold its own information such as its "username", and
 * (2) can "createOrganization()" so need to @autowired "*Repository" in.
 * But it seems impossible in the current framework.
 * 
 * Therefore, these classes will go with really procedured approach. 
 */
@Service
public class MemberServiceImpl implements MemberService {

	@Autowired MemberRepository memberRepository;
	@Autowired OrganizationRepository organizationRepository;
	@Autowired OrganizationMemberMapRepository organizationMemberMapRepository;
	
	@Override
	public MemberBean getMember(String username) {
		return memberRepository.findByUsername(username).get(0);
	}
	
	@Override
	public Collection<OrganizationBean> getManagedOrganizations(String username) {
		
		/* 
		 * I believe that Hibernate should be smart enough that when
		 * "memberRepository.findById()" is called the second time in an execution,
		 * it will not reach the database again but use the same return value.
		 * (As it march with what Martin Flower said of "Identity Map" of an ORM
		 * design). Need to double check. 
		 * 
		 * If not, we need to find out a smarter way to handle the case of listing 
		 * all organizations (we can have it iterated/filtered multiple times 
		 * when display), for example a dirty fix of implement a proxy pattern
		 * inside of the "getMember()" method
		 */
		MemberBean member = getMember(username);
		return member.getOrganizations(OrganizationMemberRole.MANAGER);
	}

	@Override
	public Collection<OrganizationBean> getBelongedOrganizations(String username) {
		
		MemberBean member = getMember(username);
		return member.getOrganizations(OrganizationMemberRole.MEMBER);
	}

	@Override
	public Collection<RepositoryBean> getOrganizedRepositories(String username) {
		
		MemberBean member = getMember(username);
		return member.getRepositories(RepositoryMemberRole.ORGANIZER);
	}

	@Override
	public Collection<RepositoryBean> getAuthoredRepositories(String username) {
		
		MemberBean member = getMember(username);
		return member.getRepositories(RepositoryMemberRole.EDITOR);
	}
	
	@Override
	public void createOrganization(String username, OrganizationDTO organizationDTO) {
		
		MemberBean member = getMember(username);
		
		OrganizationBean organization = new OrganizationBean();
		organization.setName(organizationDTO.getName());
		organization.setDisplayName(organizationDTO.getDisplayName());
		
		/*
		 * Need to save first. Otherwise when saving 
		 * "OrganizationMemberMapBean", non-null error will
		 * be raised for "organization_id" column.
		 */
		organizationRepository.saveAndFlush(organization);
		
		OrganizationMemberMapBean map = OrganizationMemberMapBean.link(organization, member, OrganizationMemberRole.MANAGER);
		
		/*
		 * Cannot using "memberRepository" or "organizationRepository"
		 * to save. It will double-insert the target row and cause primary
		 * key error.
		 */
		organizationMemberMapRepository.saveAndFlush(map);
	}
}
