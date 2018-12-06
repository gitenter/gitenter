package com.gitenter.envelope.service;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.gitenter.envelope.dto.MemberProfileDTO;
import com.gitenter.envelope.dto.MemberRegisterDTO;
import com.gitenter.envelope.dto.OrganizationDTO;
import com.gitenter.envelope.service.exception.UserNotExistException;
import com.gitenter.protease.dao.auth.MemberRepository;
import com.gitenter.protease.dao.auth.OrganizationMemberMapRepository;
import com.gitenter.protease.dao.auth.OrganizationRepository;
import com.gitenter.protease.dao.auth.SshKeyRepository;
import com.gitenter.protease.domain.auth.MemberBean;
import com.gitenter.protease.domain.auth.OrganizationBean;
import com.gitenter.protease.domain.auth.OrganizationMemberMapBean;
import com.gitenter.protease.domain.auth.OrganizationMemberRole;
import com.gitenter.protease.domain.auth.RepositoryBean;
import com.gitenter.protease.domain.auth.RepositoryMemberRole;
import com.gitenter.protease.domain.auth.SshKeyBean;

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
	@Autowired SshKeyRepository sshKeyRepository;
	
	@Autowired private PasswordEncoder passwordEncoder;
	
	@Override
	public MemberBean getMemberByUsername(String username) throws IOException {
		List<MemberBean> members = memberRepository.findByUsername(username);
		if (members.size() == 1) {
			return members.get(0);
		}
		else {
			throw new UserNotExistException(username);
		}
	}
	
	public MemberProfileDTO getMemberProfileDTO(Authentication authentication) throws IOException {
		
		MemberBean member = getMemberByUsername(authentication.getName());
		
		MemberProfileDTO profile = new MemberProfileDTO();
		profile.fillFromBean(member);
		
		return profile;
	}
	
	public MemberRegisterDTO getMemberRegisterDTO(Authentication authentication) throws IOException {
		
		MemberBean member = getMemberByUsername(authentication.getName());
		
		/*
		 * Can just use superclass method, as fulfill "password"
		 * attribute is not necessary.
		 */
		MemberRegisterDTO profileAndPassword = new MemberRegisterDTO();
		profileAndPassword.fillFromBean(member);
		
		return profileAndPassword;
	}
	
	@PreAuthorize("isAuthenticated()")
	public void updateMember(MemberProfileDTO profile) throws IOException {
		
		MemberBean memberBean = getMemberByUsername(profile.getUsername());
		profile.updateBean(memberBean);
		
		/* Since "saveAndFlush()" will decide by itself whether the operation is
		 * INSERT or UPDATE, the bean being actually modified and refreshed should 
		 * be the bean queried from the database, rather than the bean user just
		 * produced. 
		 */
		memberRepository.saveAndFlush(memberBean);
	}
	
	@PreAuthorize("isAuthenticated()")
	public boolean updatePassword(MemberRegisterDTO register, String oldPassword) throws IOException {
		
		MemberBean memberBean = getMemberByUsername(register.getUsername());
		
		if (!passwordEncoder.matches(oldPassword, memberBean.getPassword())) {
			return false;
		}
		else {
			memberBean.setPassword(passwordEncoder.encode(register.getPassword()));
			memberRepository.saveAndFlush(memberBean);
			
			return true;
		}
	}
	
	@Override
	public void createOrganization(Authentication authentication, OrganizationDTO organizationDTO) throws IOException {
		
		MemberBean member = getMemberByUsername(authentication.getName());
		OrganizationBean organization = organizationDTO.toBean();
		
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
	
	@Override
	public Collection<OrganizationBean> getManagedOrganizations(String username) throws IOException {
		
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
		MemberBean member = getMemberByUsername(username);
		return member.getOrganizations(OrganizationMemberRole.MANAGER);
	}

	@Override
	public Collection<OrganizationBean> getBelongedOrganizations(String username) throws IOException {
		
		MemberBean member = getMemberByUsername(username);
		return member.getOrganizations(OrganizationMemberRole.MEMBER);
	}

	@Override
	public Collection<RepositoryBean> getOrganizedRepositories(String username) throws IOException {
		
		MemberBean member = getMemberByUsername(username);
		return member.getRepositories(RepositoryMemberRole.ORGANIZER);
	}

	@Override
	public Collection<RepositoryBean> getAuthoredRepositories(String username) throws IOException {
		
		MemberBean member = getMemberByUsername(username);
		return member.getRepositories(RepositoryMemberRole.EDITOR);
	}
	
	@Override
	public void addSshKey(SshKeyBean sshKey, MemberBean member) throws IOException {
		
		sshKey.setMember(member);
		member.addSshKey(sshKey);
		
		sshKeyRepository.saveAndFlush(sshKey);
	}
}
