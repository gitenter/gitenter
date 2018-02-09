package enterovirus.capsid.service;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import enterovirus.protease.database.*;
import enterovirus.protease.domain.*;

@Service
public class SecurityService {

	@Autowired private MemberRepository memberRepository;
	@Autowired private OrganizationRepository organizationRepository;
	@Autowired private RepositoryRepository repositoryRepository;
	@Autowired private RepositoryMemberMapRepository repositoryMemberMapRepository;

	/*
	 * TODO:
	 * Is there are way to do this one using Spring Security JSP
	 * forms such as 
	 * <security:authentication property="principal.username" />
	 * with JSTL "if"?
	 */
	@Transactional
	public boolean checkIsMember (Authentication authentication, Integer memberId) {
		try {
			MemberBean self = memberRepository.findByUsername(authentication.getName());
			
			if (self.getId().equals(memberId)) {
				return true;
			}
			return false;
		}
		catch (IOException e) {
			return false;
		}
	}
	
	@Transactional
	public boolean checkManagerOfAnOrganization (Authentication authentication, Integer organizationId) {
		
		try {
			MemberBean self = memberRepository.findByUsername(authentication.getName());
			OrganizationBean organization = organizationRepository.findById(organizationId);
			
			if (organization.isManagedBy(self.getId())) {
				return true;
			}
			return false;
		}
		catch (IOException e) {
			return false;
		}
	}
	
	public boolean checkRepositoryReadability (Authentication authentication, Integer repositoryId) {
		
		/*
		 * If the repository is public, then this is true.
		 */
		try {
			RepositoryBean repository = repositoryRepository.findById(repositoryId);
			if (repository.getIsPublic().equals(Boolean.TRUE)) {
				return true;
			}
		}
		catch (IOException e) {
			return false;
		}

		/*
		 * Otherwise need to check whether the user belongs to one of the roles.
		 */
		Collection<RepositoryMemberRole> roles = getRepositoryRole(authentication, repositoryId);

		Collection<RepositoryMemberRole> expectRoles = new HashSet<RepositoryMemberRole>();
		expectRoles.add(RepositoryMemberRole.READER);
		expectRoles.add(RepositoryMemberRole.REVIEWER);
		expectRoles.add(RepositoryMemberRole.EDITOR);
		expectRoles.add(RepositoryMemberRole.PROJECT_LEADER);
		
		/*
		 * If "disjoint()" is "true", that means the user doesn't have 
		 * any of the roles, so s/he doesn't have reading authorization.
		 */
		return !Collections.disjoint(roles, expectRoles);
	}
	
	public boolean checkRepositoryEditability (Authentication authentication, Integer repositoryId) {

		Collection<RepositoryMemberRole> roles = getRepositoryRole(authentication, repositoryId);

		Collection<RepositoryMemberRole> expectRoles = new HashSet<RepositoryMemberRole>();
		expectRoles.add(RepositoryMemberRole.EDITOR);
		expectRoles.add(RepositoryMemberRole.PROJECT_LEADER);

		return !Collections.disjoint(roles, expectRoles);
	}
	
	private Collection<RepositoryMemberRole> getRepositoryRole (Authentication authentication, Integer repositoryId) {
		
		/*
		 * With the "UNIQUE (repository_id, member_id)" constrain in table
		 * "config.repository_member_map", there is at most one item in
		 * this list. However, we make it general for this method.
		 */
		List<RepositoryMemberMapBean> maps = repositoryMemberMapRepository.findByUsernameAndRepositoryId(authentication.getName(), repositoryId);
		
		Set<RepositoryMemberRole> roles = new HashSet<RepositoryMemberRole>();
		for (RepositoryMemberMapBean map : maps) {
			roles.add(map.getRole());
		}
		
		return roles;
	}
}
