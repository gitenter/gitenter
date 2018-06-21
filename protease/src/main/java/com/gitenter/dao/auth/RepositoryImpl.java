package com.gitenter.dao.auth;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
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
			
			File repositoryDirectory = gitSource.getBareRepositoryDirectory(
					repository.getOrganization().getName(), 
					repository.getName());
			
			GitRepository gitRepository = GitBareRepository.getInstance(repositoryDirectory);
			Collection<GitBranch> gitBranches = gitRepository.getBranches();
			
			Collection<BranchBean> branches = new ArrayList<BranchBean>();
			for (GitBranch gitBranch : gitBranches) {
				branches.add(new BranchBean(
						gitBranch.getName(), 
						repository, 
						new ProxyHeadPlaceholder(repository, gitBranch.getName())));
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
		
		/*
		 * TODO:
		 * 
		 * No need for "gitSource" as input. Could remove it in this file
		 * as it is the instance variable of the outer class.
		 */
		public ProxyHeadPlaceholder(RepositoryBean repository, String branchName) {
			this.repository = repository;
			this.branchName = branchName;
		}
		
		@Override
		public CommitBean getHead() throws IOException, GitAPIException {
			
			if (placeholder == null) {
				placeholder = new RealHeadPlaceholder(repository, branchName);
			}
			
			/*
			 * TODO:
			 * This is not right. Should set "Head" to null and set proxy.
			 */
			return placeholder.getHead();
		}
	}
	
	private class RealHeadPlaceholder implements BranchBean.HeadPlaceholder {

		private RepositoryBean repository;
		private String branchName;
		
		private CommitBean commit;
		
		public RealHeadPlaceholder(RepositoryBean repository, String branchName) throws IOException, GitAPIException {
			this.repository = repository;
			this.branchName = branchName;
			
			loadHead();
		}
		
		private void loadHead() throws IOException, GitAPIException {
			
			File repositoryDirectory = gitSource.getBareRepositoryDirectory(
					repository.getOrganization().getName(), 
					repository.getName());
			
			GitRepository gitRepository = GitBareRepository.getInstance(repositoryDirectory);
			GitCommit gitCommit = gitRepository.getBranch(branchName).getHead();
			
			CommitBean commit = commitDatabaseRepository.findByRepositoryIdAndShaChecksumHash(repository.getId(), gitCommit.getSha()).get(0);
			CommitImpl.updateFromGitCommit(commit, gitCommit);
			
			this.commit = commit;
		}
		
		@Override
		public CommitBean getHead() {
			return commit;
		}
	}
}
