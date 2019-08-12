package com.gitenter.capsid.service;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.persistence.PersistenceException;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gitenter.capsid.dto.RepositoryDTO;
import com.gitenter.capsid.service.exception.InvalidOperationException;
import com.gitenter.capsid.service.exception.RepositoryNameNotUniqueException;
import com.gitenter.gitar.GitBareRepository;
import com.gitenter.protease.config.bean.GitSource;
import com.gitenter.protease.dao.auth.MemberRepository;
import com.gitenter.protease.dao.auth.OrganizationMemberMapRepository;
import com.gitenter.protease.dao.auth.OrganizationRepository;
import com.gitenter.protease.dao.auth.RepositoryMemberMapRepository;
import com.gitenter.protease.dao.auth.RepositoryRepository;
import com.gitenter.protease.domain.auth.MemberBean;
import com.gitenter.protease.domain.auth.OrganizationBean;
import com.gitenter.protease.domain.auth.OrganizationMemberMapBean;
import com.gitenter.protease.domain.auth.RepositoryBean;
import com.gitenter.protease.domain.auth.RepositoryMemberMapBean;
import com.gitenter.protease.domain.auth.RepositoryMemberRole;

@Service
public class RepositoryManagerServiceImpl implements RepositoryManagerService {
	
	private static final Logger auditLogger = LoggerFactory.getLogger("audit");

	@Autowired MemberRepository memberRepository;
	@Autowired OrganizationRepository organizationRepository;
	@Autowired OrganizationMemberMapRepository organizationMemberMapRepository;
	@Autowired RepositoryRepository repositoryRepository;
	@Autowired RepositoryMemberMapRepository repositoryMemberMapRepository;
	
	@Autowired GitSource gitSource;
	
	/*
	 * TODO:
	 * Transaction setup in case map.saveAndFlush raises an exception, or file creation raises an
	 * exception. Notice that a simple `@Transactional` will cause the application unable
	 * to catch `RepositoryNameNotUniqueException` to redirect to the creation page. 
	 * > o.s.t.i.TransactionInterceptor : Application exception overridden by commit exception
	 */
	@Override
	@PreAuthorize("hasPermission(#organization, T(com.gitenter.protease.domain.auth.OrganizationMemberRole).MANAGER) or hasPermission(#organization, T(com.gitenter.protease.domain.auth.OrganizationMemberRole).MEMBER)")
	public void createRepository(
			MemberBean me, 
			OrganizationBean organization, 
			RepositoryDTO repositoryDTO, 
			Boolean includeSetupFiles) throws IOException, GitAPIException {
				
		RepositoryBean repository = repositoryDTO.toBean();
		
		organization.addRepository(repository);
		
		/*
		 * Need to refresh the ID of "repository", so will not
		 * work if saving by "organizationRepository".
		 */
		try {
			repositoryRepository.saveAndFlush(repository);
		}
		catch(PersistenceException e) {
			/*
			 * TODO:
			 * Seems no easy way to catch, and/or read the inside exception 
			 * `org.hibernate.exception.ConstraintViolationException`, and
			 * then `org.postgresql.util.PSQLException`, and further distinguish
			 * different exceptions?
			 * There's a separate (log) error coming from `o.h.e.jdbc.spi.SqlExceptionHelper`
			 * but it is not catchable.
			 */
			if (e.getMessage().contains("ConstraintViolationException")) {
				throw new RepositoryNameNotUniqueException(repository);
			}
			throw e;
		}
		
		RepositoryMemberMapBean map = RepositoryMemberMapBean.link(repository, me, RepositoryMemberRole.ORGANIZER);
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
	@PreAuthorize("hasPermission(#repository, T(com.gitenter.protease.domain.auth.RepositoryMemberRole).ORGANIZER)")
	public void updateRepository(
			RepositoryBean repository, 
			RepositoryDTO repositoryDTO) throws IOException {
		
		repositoryDTO.updateBean(repository);
		repositoryRepository.saveAndFlush(repository);
	}
	
	@Override
	@PreAuthorize("hasPermission(#repository, T(com.gitenter.protease.domain.auth.RepositoryMemberRole).ORGANIZER)")
	public void addCollaborator(
			RepositoryBean repository, 
			MemberBean collaborator, 
			String roleName) throws IOException {
		
		List<OrganizationMemberMapBean> maps = organizationMemberMapRepository.fineByMemberAndOrganization(
				collaborator, repository.getOrganization());
		if (maps.size() == 0) {
			throw new InvalidOperationException("User "+collaborator.getUsername()+" cannot be added "
					+ "as a collaborator for repository "+repository.getName()+", since s/he is "
					+ "not a member of organization "+repository.getOrganization().getName()+".");
		}
		
		RepositoryMemberRole role = RepositoryMemberRole.collaboratorRoleOf(roleName);
		
		RepositoryMemberMapBean map = RepositoryMemberMapBean.link(repository, collaborator, role);
		repositoryMemberMapRepository.saveAndFlush(map);
	}

	@Override
	@PreAuthorize("hasPermission(#repository, T(com.gitenter.protease.domain.auth.RepositoryMemberRole).ORGANIZER)")
	@Transactional
	public void removeCollaborator(
			RepositoryBean repository, 
			Integer repositoryMemberMapId) throws IOException {
		
		/*
		 * TODO:
		 * Should we validate the `repositoryMemberMapId`?
		 */
		
		/*
		 * The alternative approach is to have input "memberId", then
		 * find "mapId" and delete it. We don't do it because it:
		 * (1) need more SQL queries, 
		 * (2) seems have consistency problem with Hibernate when first we 
		 * "Hibernate.initialize(repository.getRepositoryMemberMaps());".
		 * 
		 * We have knowledge of `repositoryMemberMapId` when we generate
		 * the delete page with links.
		 */
		repositoryMemberMapRepository.throughSqlDeleteById(repositoryMemberMapId);
	}

	@Override
	@PreAuthorize("hasPermission(#repository, T(com.gitenter.protease.domain.auth.RepositoryMemberRole).ORGANIZER)")
	public void deleteRepository(RepositoryBean repository) throws IOException, GitAPIException {
		
		auditLogger.info("Repository has been deleted: "+repository);
		repositoryRepository.delete(repository);
	}
}
