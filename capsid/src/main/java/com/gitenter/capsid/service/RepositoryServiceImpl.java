package com.gitenter.capsid.service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.gitenter.capsid.service.exception.CommitShaNotExistException;
import com.gitenter.capsid.service.exception.IdNotExistException;
import com.gitenter.capsid.service.exception.IncludeFileNotExistException;
import com.gitenter.protease.dao.auth.RepositoryGitUpdateFactory;
import com.gitenter.protease.dao.auth.RepositoryRepository;
import com.gitenter.protease.dao.git.CommitGitUpdateFactory;
import com.gitenter.protease.dao.git.CommitRepository;
import com.gitenter.protease.dao.git.IncludeFileRepository;
import com.gitenter.protease.domain.auth.RepositoryBean;
import com.gitenter.protease.domain.auth.RepositoryUserMapBean;
import com.gitenter.protease.domain.auth.RepositoryUserRole;
import com.gitenter.protease.domain.auth.UserBean;
import com.gitenter.protease.domain.git.BranchBean;
import com.gitenter.protease.domain.git.CommitBean;
import com.gitenter.protease.domain.git.FileBean;
import com.gitenter.protease.domain.git.IncludeFileBean;
import com.gitenter.protease.domain.git.ValidCommitBean;

@Service
public class RepositoryServiceImpl implements RepositoryService {
	
	@Autowired RepositoryRepository repositoryRepository;
	@Autowired CommitRepository commitRepository;
	@Autowired IncludeFileRepository includeFileRepository;
	
	@Autowired private RepositoryGitUpdateFactory repositoryGitUpdateFactory;
	@Autowired private CommitGitUpdateFactory commitGitUpdateFactory;

	@Override
	@PostAuthorize("hasPermission(returnObject, T(com.gitenter.capsid.dto.RepositoryAccessLevel).READ)")
	public RepositoryBean getRepository(Integer repositoryId) throws IdNotExistException {
		
		Optional<RepositoryBean> repositories = repositoryRepository.findById(repositoryId);
		if (repositories.isPresent()) {
			return repositoryRepository.findById(repositoryId).get();
		}
		else {
			throw new IdNotExistException(RepositoryBean.class, repositoryId);
		}
	}
	
	@Override
	@PreAuthorize("hasPermission(#repository, T(com.gitenter.protease.domain.auth.RepositoryUserRole).PROJECT_ORGANIZER)")
	public List<RepositoryUserMapBean> getProjectOrganizerMaps(RepositoryBean repository) {
		return repository.getUserMaps(RepositoryUserRole.PROJECT_ORGANIZER);
	}
	
	@Override
	@PreAuthorize("hasPermission(#repository, T(com.gitenter.protease.domain.auth.RepositoryUserRole).PROJECT_ORGANIZER)")
	public List<RepositoryUserMapBean> getEditorMaps(RepositoryBean repository) {
		return repository.getUserMaps(RepositoryUserRole.EDITOR);
	}
	
	@Override
	@PreAuthorize("hasPermission(#repository, T(com.gitenter.protease.domain.auth.RepositoryUserRole).PROJECT_ORGANIZER)")
	public List<RepositoryUserMapBean> getBlacklistMaps(RepositoryBean repository) {
		return repository.getUserMaps(RepositoryUserRole.BLACKLIST);
	}

	@Override
	public List<UserBean> getProjectOrganizers(RepositoryBean repository) {
		return repository.getUsers(RepositoryUserRole.PROJECT_ORGANIZER);
	}
	
	@Override
	public List<UserBean> getEditors(RepositoryBean repository) {
		return repository.getUsers(RepositoryUserRole.EDITOR);
	}
	
	@Override
	public CommitBean getCommitFromBranchName(Integer repositoryId, String branchName) throws IOException, GitAPIException {
		
		BranchBean branch = getRepository(repositoryId).getBranch(branchName);
		CommitBean commit = branch.getHead();
		commitGitUpdateFactory.update(commit);
		
		return commit;
	}

	@Override
	public CommitBean getCommitFromSha(
			Integer repositoryId, 
			String commitSha) throws IOException, GitAPIException {
		
		List<CommitBean> commits = commitRepository.findByRepositoryIdAndCommitSha(repositoryId, commitSha);
		if (commits.size() == 0) {
			throw new CommitShaNotExistException(repositoryId, commitSha);
		}

		/*
		 * No possibility to have multiple returns based on
		 * SQL unique constrain.
		 */
		CommitBean commit = commits.get(0);
		
		/*
		 * Currently if we `getRepository` from a commit object,
		 * the git related placeholders in `RepositoryBean` are
		 * not bootstrapped yet.
		 */
		repositoryGitUpdateFactory.update(commit.getRepository());
		
		return commit;
	}

	@Override
	public IncludeFileBean getIncludeFileFromCommitShaAndRelativePath(
			String commitSha, 
			String relativePath) throws IOException, GitAPIException {
	
		List<IncludeFileBean> files = includeFileRepository.findByCommitShaAndRelativePath(commitSha, relativePath);
		if (files.size() == 0) {
			throw new IncludeFileNotExistException(commitSha, relativePath);
		}
		
		IncludeFileBean file = files.get(0);
		
		commitGitUpdateFactory.update(file.getCommit());
		repositoryGitUpdateFactory.update(file.getCommit().getRepository());
		
		return file;
	}

	@Override
	public IncludeFileBean getIncludeFileFromRepositoryIdAndBranchAndRelativePath(
			Integer repositoryId, 
			String branchName, 
			String relativePath) throws IOException, GitAPIException {
		
		CommitBean commit = getCommitFromBranchName(repositoryId, branchName);
		assert commit instanceof ValidCommitBean;
		ValidCommitBean validCommit = (ValidCommitBean)commit;
		
		List<IncludeFileBean> includeFiles = includeFileRepository.findByCommitShaAndRelativePath(commit.getSha(), relativePath);
		if (includeFiles.size() == 0) {
			throw new IncludeFileNotExistException(repositoryId, branchName, relativePath);
		}
		
		IncludeFileBean includeFile = includeFiles.get(0);
		includeFile.setCommit(validCommit);
		
		return includeFile;
	}

	@Override
	public FileBean getFileFromRepositoryIdAndCommitShaAndRelativePath(
			Integer repositoryId,
			String commitSha, 
			String relativePath) throws IOException, GitAPIException {
		
		CommitBean commit = getCommitFromSha(repositoryId, commitSha);
		assert commit instanceof ValidCommitBean;
		ValidCommitBean validCommit = (ValidCommitBean)commit;
		
		FileBean file = validCommit.getFile(relativePath);
		
		return file;
	}

	@Override
	public FileBean getFileFromRepositoryIdAndBranchAndRelativePath(
			Integer repositoryId, 
			String branchName, 
			String relativePath) throws IOException, GitAPIException {
		
		CommitBean commit = getCommitFromBranchName(repositoryId, branchName);
		assert commit instanceof ValidCommitBean;
		ValidCommitBean validCommit = (ValidCommitBean)commit;
		
		FileBean file = validCommit.getFile(relativePath);
		
		return file;
	}
}
