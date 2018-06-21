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
import com.gitenter.dao.git.CommitImpl;
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
			item.setBranchesPlaceholder(new ProxyBranchesPlaceholder(item));
		}
		
		return items;
	}
	
	public List<RepositoryBean> findByOrganizationNameAndRepositoryName(String organizationName, String repositoryName) throws IOException {
		
		List<RepositoryBean> items = repositoryDatabaseRepository.findByOrganizationNameAndRepositoryName(organizationName, repositoryName);
		
		for (RepositoryBean item : items) {
			item.setBranchesPlaceholder(new ProxyBranchesPlaceholder(item));
		}
		
		return items;
	}
	
	public RepositoryBean saveAndFlush(RepositoryBean repository) {
		return repositoryDatabaseRepository.saveAndFlush(repository);
	}
	
//	public Collection<BranchBean> getBranches(RepositoryBean repository) throws IOException, GitAPIException {
//		return repositoryGitDAO.getBranches(repository);
//	}
//	
//	public List<CommitBean> getLog(RepositoryBean repository, BranchBean branch, Integer maxCount, Integer skip) throws IOException, GitAPIException {
//		return repositoryGitDAO.getLog(repository, branch, maxCount, skip);
//	}
	
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
				branches.add(new BranchBean(
						gitBranch.getName(), 
						repository, 
						new ProxyHeadPlaceholder(repository, gitBranch.getName()),
						new ProxyLogPlaceholder(repository, gitBranch.getName())));
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
	
	private class ProxyLogPlaceholder implements BranchBean.LogPlaceholder {

		private RealLogPlaceholder placeholder = null;

		private RepositoryBean repository;
		private String branchName;
		
		public ProxyLogPlaceholder(RepositoryBean repository, String branchName) {
			this.repository = repository;
			this.branchName = branchName;
		}
		
		@Override
		public List<CommitBean> getLog(Integer maxCount, Integer skip) throws IOException, GitAPIException {
			
			if (placeholder == null) {
				placeholder = new RealLogPlaceholder(repository, branchName);
			}
			
			return placeholder.getLog(maxCount, skip);
		}
	}
	
	private class RealLogPlaceholder implements BranchBean.LogPlaceholder {

		private RepositoryBean repository;
		private String branchName;
		
		public RealLogPlaceholder(RepositoryBean repository, String branchName) {
			this.repository = repository;
			this.branchName = branchName;
		}
		
		/*
		 * When input is different, this method just need to run again (git will be queried 
		 * again). There's no way to avoid that. 
		 */
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
	
	private GitRepository getGitRepository (RepositoryBean repository) throws IOException, GitAPIException {
		
		File repositoryDirectory = gitSource.getBareRepositoryDirectory(
				repository.getOrganization().getName(), 
				repository.getName());
		
		return GitBareRepository.getInstance(repositoryDirectory);
	}
}
