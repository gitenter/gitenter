package com.gitenter.dao.git;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.gitenter.dao.auth.RepositoryRepository;
import com.gitenter.domain.auth.RepositoryBean;
import com.gitenter.domain.git.AuthorBean;
import com.gitenter.domain.git.BranchBean;
import com.gitenter.domain.git.CommitBean;
import com.gitenter.gitar.*;
import com.gitenter.protease.source.GitSource;

@Repository
public class CommitImpl implements CommitRepository {

	@Autowired private CommitDatabaseRepository commitDbRepository;
	@Autowired private RepositoryRepository repositoryRepository;
	@Autowired private GitSource gitSource;
	
	public Optional<CommitBean> findById(Integer id) throws IOException, GitAPIException {
		
		Optional<CommitBean> items = commitDbRepository.findById(id);
		
		if (items.isPresent()) {
			CommitBean item = items.get();
			updateFromGit(item);
		}
		
		return items;
	}
	
	public List<CommitBean> findByRepositoryIdAndCommitSha(Integer repositoryId, String commitSha) throws IOException, GitAPIException {
		
		List<CommitBean> items = commitDbRepository.findByRepositoryIdAndShaChecksumHash(repositoryId, commitSha);
		
		/*
		 * TODO:
		 * This approach is not good, as it will query from git multiple times.
		 * think about a way that it can be done in one single git query.
		 */
		for (CommitBean item : items) {
			updateFromGit(item);
		}
		
		return items;
	}
	
	public List<CommitBean> findByRepositoryIdAndCommitShaIn(Integer repositoryId, List<String> commitShas) throws IOException, GitAPIException {
		
		List<CommitBean> items = commitDbRepository.findByRepositoryIdAndShaChecksumHashIn(repositoryId, commitShas);
		
		for (CommitBean item : items) {
			updateFromGit(item);
		}
		
		return items;
	}
	
	public CommitBean findByRepositoryIdAndBranch(Integer repositoryId, BranchBean branch) throws IOException, GitAPIException {

		/*
		 * TODO:
		 * Should be a better way rather than query the database twice?
		 */
		RepositoryBean repositoryBean = repositoryRepository.findById(repositoryId).get();
		File repositoryDirectory = gitSource.getBareRepositoryDirectory(
				repositoryBean.getOrganization().getName(), 
				repositoryBean.getName());
		
		GitRepository gitRepository = GitBareRepository.getInstance(repositoryDirectory);
		GitCommit gitCommit = gitRepository.getBranch(branch.getName()).getHead();
		
		List<CommitBean> commits = commitDbRepository.findByRepositoryIdAndShaChecksumHash(repositoryId, gitCommit.getSha());
		
		if (commits.size() == 0) {
			throw new IOException ("SHA checksum hash "+gitCommit.getSha()+" is not correct!");
		}
		if (commits.size() > 1) {
			throw new IOException ("SHA checksum hash "+gitCommit.getSha()+" is not unique!");
		}
		
		CommitBean commit = commits.get(0);
		updateFromGitCommit(commit, gitCommit);
		
		return commit;
	}
	
	private void updateFromGit(CommitBean commit) throws IOException, GitAPIException {
		
		File repositoryDirectory = gitSource.getBareRepositoryDirectory(
				commit.getRepository().getOrganization().getName(), 
				commit.getRepository().getName());
		
		GitRepository gitRepository = GitBareRepository.getInstance(repositoryDirectory);
		GitCommit gitCommit = gitRepository.getCommit(commit.getShaChecksumHash());
		
		updateFromGitCommit(commit, gitCommit);
	}
	
	public static void updateFromGitCommit(CommitBean commit, GitCommit gitCommit) {
		
		commit.setTimestamp(gitCommit.getTimestamp());
		commit.setMessage(gitCommit.getMessage());
		commit.setAuthor(new AuthorBean(gitCommit.getAuthor()));
	}
	
	public CommitBean saveAndFlush(CommitBean commit) {
		return commitDbRepository.saveAndFlush(commit);
	}
}
