package com.gitenter.envelope.service;

import java.io.File;
import java.io.IOException;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.gitenter.envelope.dto.RepositoryDTO;
import com.gitenter.gitar.GitBareRepository;
import com.gitenter.protease.dao.auth.MemberRepository;
import com.gitenter.protease.dao.auth.OrganizationRepository;
import com.gitenter.protease.dao.auth.RepositoryMemberMapRepository;
import com.gitenter.protease.dao.auth.RepositoryRepository;
import com.gitenter.protease.domain.auth.MemberBean;
import com.gitenter.protease.domain.auth.OrganizationBean;
import com.gitenter.protease.domain.auth.RepositoryBean;
import com.gitenter.protease.domain.auth.RepositoryMemberMapBean;
import com.gitenter.protease.domain.auth.RepositoryMemberRole;
import com.gitenter.protease.source.GitSource;

@Service
public class RepositoryManagerServiceImpl implements RepositoryManagerService {

	@Autowired MemberRepository memberRepository;
	@Autowired OrganizationRepository organizationRepository;
	@Autowired RepositoryRepository repositoryRepository;
	@Autowired RepositoryMemberMapRepository repositoryMemberMapRepository;
	
	@Autowired GitSource gitSource;
	
	@PreAuthorize("hasPermission(#organizationId, T(com.gitenter.protease.domain.auth.OrganizationMemberRole).MANAGER) or hasPermission(#organizationId, T(com.gitenter.protease.domain.auth.OrganizationMemberRole).MEMBER)")
	@Override
	public void createRepository(Authentication authentication, Integer organizationId, RepositoryDTO repositoryDTO, Boolean includeSetupFiles) throws IOException, GitAPIException {
		
		RepositoryBean repository = new RepositoryBean();
		repositoryDTO.updateRepositoryBean(repository);
		
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

	@Override
	public void updateRepository(Authentication authentication, RepositoryBean repositoryBean, RepositoryDTO repositoryDTO) throws IOException {
		
		repositoryDTO.updateRepositoryBean(repositoryBean);
		repositoryRepository.saveAndFlush(repositoryBean);
	}
}
