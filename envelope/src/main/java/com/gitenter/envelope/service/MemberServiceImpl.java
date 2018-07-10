package com.gitenter.envelope.service;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gitenter.protease.dao.auth.MemberRepository;
import com.gitenter.protease.domain.auth.MemberBean;
import com.gitenter.protease.domain.auth.OrganizationBean;
import com.gitenter.protease.domain.auth.OrganizationMemberRole;
import com.gitenter.protease.domain.auth.RepositoryBean;
import com.gitenter.protease.domain.auth.RepositoryMemberRole;

@Service
public class MemberServiceImpl implements MemberService {

	@Autowired private MemberRepository memberRepository;
	
	@Override
	public Collection<OrganizationBean> getManagedOrganizations(String username) {
		
		MemberBean member = memberRepository.findByUsername(username).get(0);
		return member.getOrganizations(OrganizationMemberRole.MANAGER);
	}

	@Override
	public Collection<OrganizationBean> getAccessibleOrganizations(String username) {
		
		/* 
		 * I believe that Hibernate should be smart enough that when
		 * "memberRepository.findById()" is called the second time in an execution,
		 * it will not reach the database again but use the same return value.
		 * (As it march with what Martin Flower said of "Identity Map" of an ORM
		 * design). Need to double check. 
		 * 
		 * If not, we need to find out a smarter way to handle the case of listing 
		 * all organizations (we can have it iterated/filtered multiple times 
		 * when display).
		 */
		MemberBean member = memberRepository.findByUsername(username).get(0);
		return member.getOrganizations(OrganizationMemberRole.MEMBER);
	}

	@Override
	public Collection<RepositoryBean> getOrganizedRepositories(String username) {
		
		MemberBean member = memberRepository.findByUsername(username).get(0);
		return member.getRepositories(RepositoryMemberRole.ORGANIZER);
	}

	@Override
	public Collection<RepositoryBean> getEditableRepositories(String username) {
		
		MemberBean member = memberRepository.findByUsername(username).get(0);
		return member.getRepositories(RepositoryMemberRole.EDITOR);
	}

}
