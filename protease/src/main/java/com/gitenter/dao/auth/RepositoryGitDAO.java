package com.gitenter.dao.auth;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.gitenter.dao.git.CommitDatabaseRepository;
import com.gitenter.domain.auth.RepositoryBean;
import com.gitenter.domain.git.AuthorBean;
import com.gitenter.domain.git.BranchBean;
import com.gitenter.domain.git.CommitBean;
import com.gitenter.gitar.GitBareRepository;
import com.gitenter.gitar.GitBranch;
import com.gitenter.gitar.GitCommit;
import com.gitenter.gitar.GitRepository;
import com.gitenter.protease.source.GitSource;

/*
 * Functions of this class is not merged into RepositoryRepository,
 * is because some usage of RepositoryBean doesn't need the git data.
 * This makes some kind of lazy evaluation.
 * 
 * TODO:
 * Can it be done in the way that it loads the Git data when calling 
 * "repository.getCommitLog()" and "repository.getBranchNames()" ?
 */
@Repository
public class RepositoryGitDAO {

	@Autowired private GitSource gitSource;
	@Autowired private CommitDatabaseRepository commitDbRepository;
	
	public Collection<BranchBean> getBranches (RepositoryBean repository) throws IOException, GitAPIException {
		
		File repositoryDirectory = gitSource.getBareRepositoryDirectory(
				repository.getOrganization().getName(), 
				repository.getName());
		
		GitRepository gitRepository = GitBareRepository.getInstance(repositoryDirectory);
		Collection<GitBranch> gitBranches = gitRepository.getBranches();
		
		Collection<BranchBean> branches = new ArrayList<BranchBean>();
		for (GitBranch gitBranch : gitBranches) {
			branches.add(new BranchBean(gitBranch.getName()));
		}
		
		return branches;
	}
	
	public List<CommitBean> getLog(RepositoryBean repository, BranchBean branch, Integer maxCount, Integer skip) throws IOException, GitAPIException {
		
		File repositoryDirectory = gitSource.getBareRepositoryDirectory(
				repository.getOrganization().getName(), 
				repository.getName());
		
		GitRepository gitRepository = GitBareRepository.getInstance(repositoryDirectory);
		GitBranch gitBranch = gitRepository.getBranch(branch.getName());
		List<GitCommit> log = gitBranch.getLog(maxCount, skip);
		
		/*
		 * Keep insert order.
		 */
		LinkedHashMap <String,GitCommit> logMap = new LinkedHashMap <String,GitCommit>();
		for (GitCommit gitCommit : log) {
			logMap.put(gitCommit.getSha(), gitCommit);
		}
		/*
		 * TODO:
		 * Need to double check whether it indeed keep orders.
		 */
		List<String> shas = new ArrayList<>(logMap.keySet());

		/*
		 * Do it in one single SQL query by performance concerns.
		 * Also, use directory database query so git information is not
		 * automatically included.
		 */
		List<CommitBean> commits = commitDbRepository.findByRepositoryIdAndShaChecksumHashIn(repository.getId(), shas);
		
		for (CommitBean commit : commits) {
			commit.setTimestamp(logMap.get(commit.getShaChecksumHash()).getTimestamp());
			commit.setMessage(logMap.get(commit.getShaChecksumHash()).getMessage());
			commit.setAuthor(new AuthorBean(logMap.get(commit.getShaChecksumHash()).getAuthor()));
		}
		
		return commits;
	}
}
