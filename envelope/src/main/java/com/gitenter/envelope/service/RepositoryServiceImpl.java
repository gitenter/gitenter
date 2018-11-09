package com.gitenter.envelope.service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gitenter.envelope.service.exception.CommitShaNotExistException;
import com.gitenter.envelope.service.exception.IdNotExistException;
import com.gitenter.protease.dao.auth.RepositoryRepository;
import com.gitenter.protease.dao.git.CommitRepository;
import com.gitenter.protease.domain.auth.RepositoryBean;
import com.gitenter.protease.domain.git.BranchBean;
import com.gitenter.protease.domain.git.CommitBean;

@Service
public class RepositoryServiceImpl implements RepositoryService {
	
	@Autowired RepositoryRepository repositoryRepository;
	@Autowired CommitRepository commitRepository;

	/*
	 * TODO:
	 * 
	 * User can only access repository information if she is authorized
	 * to do so.
	 */
	@Override
	public RepositoryBean getRepository(Integer repositoryId) throws IdNotExistException {
		
		Optional<RepositoryBean> repositories = repositoryRepository.findById(repositoryId);
		if (repositories.isPresent()) {
			return repositoryRepository.findById(repositoryId).get();
		}
		else {
			throw new IdNotExistException("repository", repositoryId);
		}
	}

	@Override
	public CommitBean getCommitFromBranchName(Integer repositoryId, String branchName) throws IOException, GitAPIException {
		
		/*
		 * TODO:
		 * 
		 * Here didn't load the folder structure of the commit. So if we
		 * `commit.getRoot()`, it will return None and cause error.
		 * Starting from `commitRepository` will setup document structure
		 * placeholder through.
		 * 
		 * However, we don't need document structure every time. Try a hybrid
		 * approach to load the document structure when needed.
		 */
		BranchBean branch = getRepository(repositoryId).getBranch(branchName);
		return branch.getHead();
	}

	@Override
	public CommitBean getCommitFromSha(Integer repositoryId, String commitSha) throws IOException, GitAPIException {
		
		List<CommitBean> commits = commitRepository.findByRepositoryIdAndCommitSha(repositoryId, commitSha);
		if (commits.size() == 0) {
			throw new CommitShaNotExistException(repositoryId, commitSha);
		}
		else {
			/*
			 * No possibility to have multiple returns based on
			 * SQL unique constrain.
			 */
			CommitBean commit = commits.get(0);
			
			/*
			 * Currently if we `getRepository` from a commit object,
			 * the git related placeholders are not bootstrapped yet.
			 * 
			 * It is technically really hard to do it in the DAO layer
			 * (as you don't want `CommitRepositoryImpl` to rely on 
			 * `RepositoryRepositoryImpl` (not `RepositoryDatabaseRepository`),
			 * so we make up the fact in here.
			 */
			RepositoryBean repository = getRepository(commit.getRepository().getId());
			commit.setRepository(repository);
			
			return commit;
		}
	}
}
