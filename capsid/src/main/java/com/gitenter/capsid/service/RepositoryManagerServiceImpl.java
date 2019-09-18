package com.gitenter.capsid.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.attribute.FileOwnerAttributeView;
import java.nio.file.attribute.UserPrincipal;
import java.nio.file.attribute.UserPrincipalLookupService;
import java.nio.file.attribute.UserPrincipalNotFoundException;
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
			ExceptionConsumingPipeline.consumePersistenceException(e, repository);
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
	
		/*
		 * To transfer the ownership of the git bare folder from tomcat runtime user 
		 * (root in Docker web container) to user `git`, as `git` is used as the execution user
		 * in Docker git container and we are doing `git clone git@...`. This is to avoid the
		 * problem git bare folder has permission `drwxr-x---` and `git push` will give error:
		 * > error: remote unpack failed: unable to create temporary object directory
		 * > To git:/home/git/asdf/asdf.git
		 * >  ! [remote rejected] master -> master (unpacker error)
		 * > error: failed to push some refs to 'git@git:/home/git/asdf/asdf.git'
		 * 
		 * Confirmed Linux only care about user name string, so users with same name `git` in 
		 * different containers/runtime OS share the same folder will assume each other the same 
		 * user.
		 * 
		 * Con of this approach include: we can no longer edit the git repository through web
		 * interface, which is running by a different user then.
		 * 
		 * Alternative approaches include:
		 * - Run tomcat using user git. Need a customized docker image, and also counter-intuative.
		 * - Use `git clone root@...` but need to allow root login first.
		 */
		chownR(repositoryDirectory);
	}
	
	private void chownR(File file) throws IOException {
		chown(file);
		if (file.isDirectory()) {
			for (File subFile : file.listFiles()) {
				chownR(subFile);
			}
		}
	}
	
	private void chown(File file) throws IOException {
		try {
			FileSystem fs = FileSystems.getDefault();
			UserPrincipalLookupService upls = fs.getUserPrincipalLookupService();
			UserPrincipal gitUser = upls.lookupPrincipalByName("git");
			
			FileOwnerAttributeView foav = Files.getFileAttributeView(file.toPath(), FileOwnerAttributeView.class);
			foav.setOwner(gitUser);
		}
		catch (UserPrincipalNotFoundException e) {
			/*
			 * Error out in local. However, we don't want to make change in local either. 
			 * Therefore we want to swallow this error.
			 * 
			 * There are two fold of errors in local:
			 * (1) `git` user doesn't exit.
			 * (2) (After `git` user is setting up) Java is running on no-sudo and cannot `setOwner`:
			 * Maybe a mac only problem.
			 * 
			 * TODO:
			 * May conditionally execute this block based on profile, so error can still be raised
			 * in containerized environments.
			 */
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
