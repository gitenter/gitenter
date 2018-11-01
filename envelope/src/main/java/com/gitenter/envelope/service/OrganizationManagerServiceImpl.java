package com.gitenter.envelope.service;

import java.io.File;
import java.io.IOException;
import java.util.ListIterator;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gitenter.envelope.dto.RepositoryDTO;
import com.gitenter.gitar.GitBareRepository;
import com.gitenter.protease.dao.auth.MemberRepository;
import com.gitenter.protease.dao.auth.OrganizationMemberMapRepository;
import com.gitenter.protease.dao.auth.OrganizationRepository;
import com.gitenter.protease.dao.auth.RepositoryMemberMapRepository;
import com.gitenter.protease.dao.auth.RepositoryRepository;
import com.gitenter.protease.domain.auth.MemberBean;
import com.gitenter.protease.domain.auth.OrganizationBean;
import com.gitenter.protease.domain.auth.OrganizationMemberMapBean;
import com.gitenter.protease.domain.auth.OrganizationMemberRole;
import com.gitenter.protease.domain.auth.RepositoryBean;
import com.gitenter.protease.domain.auth.RepositoryMemberMapBean;
import com.gitenter.protease.domain.auth.RepositoryMemberRole;
import com.gitenter.protease.exception.RepositoryNameNotUniqueException;
import com.gitenter.protease.source.GitSource;

@Service
public class OrganizationManagerServiceImpl implements OrganizationManagerService {
	
	@Autowired MemberRepository memberRepository;
	@Autowired OrganizationRepository organizationRepository;
	@Autowired OrganizationMemberMapRepository organizationMemberMapRepository;
	@Autowired RepositoryRepository repositoryRepository;
	@Autowired RepositoryMemberMapRepository repositoryMemberMapRepository;
	
	@Autowired GitSource gitSource;
	
	@PreAuthorize("hasPermission(#organizationId, T(com.gitenter.protease.domain.auth.OrganizationMemberRole).MANAGER)")
	@Override
	public void addOrganizationMember(Integer organizationId, String username) {
		
		OrganizationBean organization = organizationRepository.findById(organizationId).get();
		MemberBean member = memberRepository.findByUsername(username).get(0);
		OrganizationMemberMapBean map = OrganizationMemberMapBean.link(organization, member, OrganizationMemberRole.MEMBER);
		organizationMemberMapRepository.saveAndFlush(map);
	}
	
	@PreAuthorize("hasPermission(#organizationId, T(com.gitenter.protease.domain.auth.OrganizationMemberRole).MANAGER)")
	@Transactional
	@Override
	public void removeOrganizationMember(Integer organizationId, String username) {
		
		OrganizationBean organization = organizationRepository.findById(organizationId).get();
		System.out.println(organization.getOrganizationMemberMaps().size());
		
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
	
	@PreAuthorize("hasPermission(#organizationId, T(com.gitenter.protease.domain.auth.OrganizationMemberRole).MANAGER)")
	@Override
	public void addOrganizationManager(Integer organizationId, String username) {
		
		OrganizationBean organization = organizationRepository.findById(organizationId).get();
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
	
	@PreAuthorize("hasPermission(#organizationId, T(com.gitenter.protease.domain.auth.OrganizationMemberRole).MANAGER)")
	@Override
	public void removeOrganizationManager(Integer organizationId, String username) {
	
		OrganizationBean organization = organizationRepository.findById(organizationId).get();
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

	@PreAuthorize("hasPermission(#organizationId, T(com.gitenter.protease.domain.auth.OrganizationMemberRole).MANAGER) or hasPermission(#organizationId, T(com.gitenter.protease.domain.auth.OrganizationMemberRole).MEMBER)")
	@Override
	public void createRepository(Authentication authentication, Integer organizationId, RepositoryDTO repositoryDTO, Boolean includeSetupFiles) throws IOException, GitAPIException, RepositoryNameNotUniqueException {
		
		RepositoryBean repository = new RepositoryBean();
		repository.setName(repositoryDTO.getName());
		repository.setDisplayName(repositoryDTO.getDisplayName());
		repository.setDescription(repositoryDTO.getDescription());
		repository.setIsPublic(repositoryDTO.getIsPublic());
		
		OrganizationBean organization = organizationRepository.findById(organizationId).get();
		organization.addRepository(repository);
		
		/*
		 * Need to refresh the ID of "repository", so will not
		 * work if saving by "organizationRepository".
		 */
		repositoryRepository.saveAndFlush(repository);
		
		MemberBean member = memberRepository.findByUsername(authentication.getName()).get(0);
		RepositoryMemberMapBean map = RepositoryMemberMapBean.link(repository, member, RepositoryMemberRole.ORGANIZER);
		repositoryMemberMapRepository.saveAndFlush(map);
		
		File repositoryDirectory = gitSource.getBareRepositoryDirectory(organization.getName(), repository.getName());
		
		/*
		 * TODO:
		 * Consider move the git related part to a DAO method.
		 * 
		 * TODO:
		 * Consider using task queue to implement git related operations.
		 */
		GitBareRepository gitRepository = GitBareRepository.getInstance(repositoryDirectory);
		
		/*
		 * TODO:
		 * Consider just setup a symlink in here. And the actual `.jar` file goes to a
		 * different place (in the Chef setup).
		 */
		ClassLoader classLoader = getClass().getClassLoader();
		File sampleHooksDirectory = new File(classLoader.getResource("git-server-side-hooks").getFile());
		gitRepository.addHooks(sampleHooksDirectory);
		
//		File configFilesDirectory = new File(classLoader.getResource("config-files").getFile());
		
		if (includeSetupFiles.equals(Boolean.FALSE)) {
		}
		else {
//			/*
//			 * Here makes a bare repository with one commit (include the
//			 * configuration file) in it.
//			 *
//			 * Dirty but this part can only be done in here. See comments under GitRepository.
//			 */
//			GitLog gitLog = new GitLog(repositoryDirectory, new BranchName("master"), 1, 0);
//			CommitSha commitSha = gitLog.getCommitInfos().get(0).getCommitSha();
//			CommitBean commit = new ValidCommitBean(repository, commitSha);
//			repository.addCommit(commit);
		}
	}
}
