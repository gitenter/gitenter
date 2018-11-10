package com.gitenter.protease.dao.git;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.gitenter.protease.domain.git.CommitBean;

@Repository
public class CommitRepositoryImpl implements CommitRepository {

	@Autowired private CommitDatabaseRepository commitDatabaseRepository;
	@Autowired private CommitGitUpdateFactory commitGitUpdateFactory;
	
	public Optional<CommitBean> findById(Integer id) throws IOException, GitAPIException {
		
		Optional<CommitBean> items = commitDatabaseRepository.findById(id);
		
		if (items.isPresent()) {
			CommitBean item = items.get();
			commitGitUpdateFactory.update(item);
		}
		
		return items;
	}
	
	public List<CommitBean> findByRepositoryIdAndCommitSha(Integer repositoryId, String commitSha) throws IOException, GitAPIException {
		
		List<CommitBean> items = commitDatabaseRepository.findByRepositoryIdAndSha(repositoryId, commitSha);
		
		for (CommitBean item : items) {
			commitGitUpdateFactory.update(item);
		}
		
		return items;
	}
	
	public List<CommitBean> findByRepositoryIdAndCommitShaIn(Integer repositoryId, List<String> commitShas) throws IOException, GitAPIException {
		
		List<CommitBean> items = commitDatabaseRepository.findByRepositoryIdAndShaIn(repositoryId, commitShas);
		
		/*
		 * TODO:
		 * This approach is not good, as it will query from git multiple times.
		 * think about a way that it can be done in one single git query.
		 */
		for (CommitBean item : items) {
			commitGitUpdateFactory.update(item);
		}
		
		return items;
	}
	
	public void deleteById(Integer id) {
		commitDatabaseRepository.deleteById(id);
	}
	
	public CommitBean saveAndFlush(CommitBean commit) {
		return commitDatabaseRepository.saveAndFlush(commit);
	}
}
