package com.gitenter.dao.auth;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.gitenter.dao.git.CommitDatabaseRepository;
import com.gitenter.domain.auth.RepositoryBean;
import com.gitenter.domain.git.BranchBean;
import com.gitenter.domain.git.CommitBean;
import com.gitenter.gitar.GitBareRepository;
import com.gitenter.gitar.GitBranch;
import com.gitenter.gitar.GitCommit;
import com.gitenter.gitar.GitRepository;
import com.gitenter.protease.source.GitSource;

@Repository
class RepositoryImpl implements RepositoryRepository {

	@Autowired private RepositoryDatabaseRepository repositoryDatabaseRepository;
	@Autowired private CommitDatabaseRepository commitDatabaseRepository;
	@Autowired private GitSource gitSource;
	
	public Optional<RepositoryBean> findById(Integer id) throws IOException {
		
		Optional<RepositoryBean> items = repositoryDatabaseRepository.findById(id); 
		
		if (items.isPresent()) {
			RepositoryBean item = items.get();
			updatePlaceholders(item);
		}
		
		return items;
	}
	
	public List<RepositoryBean> findByOrganizationNameAndRepositoryName(String organizationName, String repositoryName) throws IOException {
		
		List<RepositoryBean> items = repositoryDatabaseRepository.findByOrganizationNameAndRepositoryName(organizationName, repositoryName);
		
		for (RepositoryBean item : items) {
			updatePlaceholders(item);
		}
		
		return items;
	}
	
	private void updatePlaceholders(RepositoryBean item) {
		item.setBranchPlaceholder(new BranchPlaceholderImpl(item));
		item.setBranchesPlaceholder(new ProxyBranchesPlaceholder(item));
	}
	
	public RepositoryBean saveAndFlush(RepositoryBean repository) {
		return repositoryDatabaseRepository.saveAndFlush(repository);
	}
	
	/*
	 * No need to use proxy pattern in here, since to execute the constructor
	 * is so cheap. The reason not implement the logic in "RepositoryBean", 
	 * is because we want to keep "RepositoryBean" to have no knowledge on
	 * "ProxyHeadPlaceholder" or "ProxyLogPlaceholder".
	 */
	private class BranchPlaceholderImpl implements RepositoryBean.BranchPlaceholder {

		private RepositoryBean repository;
		
		public BranchPlaceholderImpl(RepositoryBean repository) {
			this.repository = repository;
		}
		
		@Override
		public BranchBean getBranch(String branchName) {
			return getBranchBean(repository, branchName);
		}
		
	}
	
	/*
	 * TODO:
	 * Here are a lot of boilerplate code which follows the same pattern.
	 * Consider refactoring to reuse most setups. 
	 */
	private class ProxyBranchesPlaceholder implements RepositoryBean.BranchesPlaceholder {

		private RealBranchesPlaceholder placeholder = null;

		private RepositoryBean repository;
		
		public ProxyBranchesPlaceholder(RepositoryBean repository) {
			this.repository = repository;
		}
		
		@Override
		public Collection<BranchBean> getBranches() throws IOException, GitAPIException {
			
			if (placeholder == null) {
				placeholder = new RealBranchesPlaceholder(repository);
			}
			
			return placeholder.getBranches();
		}
	}
	
	private class RealBranchesPlaceholder implements RepositoryBean.BranchesPlaceholder {

		/*
		 * Technically we cannot @Autowired "gitSource" in this 
		 * class (no matter whether it is a normal class or inner class)
		 * because Spring @Component doesn't accept a non-trivial
		 * constructor with >=1 arguments. 
		 * 
		 * So if it is a normal class, we need to get gitSource from
		 * repository/DAO and pass the information all the way done
		 * to here.
		 * 
		 * As a inner class, we can simply get the value of gitSource
		 * from an outer class instance variable.
		 */
		private RepositoryBean repository;
		
		private Collection<BranchBean> branches;
		
		public RealBranchesPlaceholder(RepositoryBean repository) throws IOException, GitAPIException {
			this.repository = repository;
			
			loadBranches();
		}
		
		private void loadBranches() throws IOException, GitAPIException {
			
			GitRepository gitRepository = getGitRepository(repository);
			Collection<GitBranch> gitBranches = gitRepository.getBranches();
			
			Collection<BranchBean> branches = new ArrayList<BranchBean>();
			for (GitBranch gitBranch : gitBranches) {
				branches.add(getBranchBean(repository, gitBranch.getName()));
			}
			
			this.branches = branches;
		}
		
		@Override
		public Collection<BranchBean> getBranches() {
			return branches;
		}
	}
	
	private class ProxyHeadPlaceholder implements BranchBean.HeadPlaceholder {

		private RealHeadPlaceholder placeholder = null;

		private RepositoryBean repository;
		private String branchName;
		
		public ProxyHeadPlaceholder(RepositoryBean repository, String branchName) {
			this.repository = repository;
			this.branchName = branchName;
		}
		
		@Override
		public CommitBean getHead() throws IOException, GitAPIException {
			
			if (placeholder == null) {
				placeholder = new RealHeadPlaceholder(repository, branchName);
			}
			
			return placeholder.getHead();
		}
	}
	
	private class RealHeadPlaceholder implements BranchBean.HeadPlaceholder {

		private RepositoryBean repository;
		private String branchName;
		
		private CommitBean head;
		
		public RealHeadPlaceholder(RepositoryBean repository, String branchName) throws IOException, GitAPIException {
			this.repository = repository;
			this.branchName = branchName;
			
			loadHead();
		}
		
		private void loadHead() throws IOException, GitAPIException {
			
			GitRepository gitRepository = getGitRepository(repository);
			GitCommit gitCommit = gitRepository.getBranch(branchName).getHead();
			
			CommitBean commit = commitDatabaseRepository.findByRepositoryIdAndSha(repository.getId(), gitCommit.getSha()).get(0);
			commit.updateFromGitCommit(gitCommit);
			
			this.head = commit;
		}
		
		@Override
		public CommitBean getHead() {
			return head;
		}
	}
	
	/*
	 * No need to use proxy pattern in here, since to execute the constructor
	 * is so cheap. 
	 * 
	 * When input is different, the implemented method just need to run again (git 
	 * will be queried again). There's no easy way to avoid that (the other possibility 
	 * is to has a caching layer in this class -- a hashtable -- to save the existing 
	 * queries and reuse them if possible, but we still cannot promote it to the 
	 * constructor and use proxy pattern to optimized an easy evaluation). 
	 */
	private class LogPlaceholderImpl implements BranchBean.LogPlaceholder {

		private RepositoryBean repository;
		private String branchName;
		
		public LogPlaceholderImpl(RepositoryBean repository, String branchName) {
			this.repository = repository;
			this.branchName = branchName;
		}
		
		@Override
		public List<CommitBean> getLog(Integer maxCount, Integer skip) throws IOException, GitAPIException {
			GitRepository gitRepository = getGitRepository(repository);
			List<GitCommit> gitLog = gitRepository.getBranch(branchName).getLog();
			
			/*
			 * Keep insert order.
			 */
			LinkedHashMap <String,GitCommit> logMap = new LinkedHashMap <String,GitCommit>();
			for (GitCommit gitCommit : gitLog) {
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
			List<CommitBean> log = commitDatabaseRepository.findByRepositoryIdAndShaIn(repository.getId(), shas);
			
			for (CommitBean commit : log) {
				commit.updateFromGitCommit(logMap.get(commit.getSha()));
			}
			
			return log;
		}
	}
	
	private BranchBean getBranchBean(RepositoryBean repository, String branchName) {
		return new BranchBean(
				branchName, 
				repository, 
				new ProxyHeadPlaceholder(repository, branchName),
				new LogPlaceholderImpl(repository, branchName));
	}
	
	private GitRepository getGitRepository (RepositoryBean repository) throws IOException, GitAPIException {
		
		File repositoryDirectory = gitSource.getBareRepositoryDirectory(
				repository.getOrganization().getName(), 
				repository.getName());
		
		return GitBareRepository.getInstance(repositoryDirectory);
	}
}
