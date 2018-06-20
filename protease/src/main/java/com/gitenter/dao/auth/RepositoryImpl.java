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

import com.gitenter.domain.auth.RepositoryBean;
import com.gitenter.domain.git.BranchBean;
import com.gitenter.domain.git.CommitBean;
import com.gitenter.gitar.GitBareRepository;
import com.gitenter.gitar.GitBranch;
import com.gitenter.gitar.GitRepository;
import com.gitenter.protease.source.GitSource;

@Repository
class RepositoryImpl implements RepositoryRepository {

	@Autowired private RepositoryDatabaseRepository repositoryDbRepository;
	@Autowired private GitSource gitSource;
	
	public Optional<RepositoryBean> findById(Integer id) throws IOException {
		
		Optional<RepositoryBean> items = repositoryDbRepository.findById(id); 
		
		if (items.isPresent()) {
			RepositoryBean item = items.get();
			item.setBranchList(new ProxyBranchList(gitSource, item));
		}
		
		return items;
	}
	
	public List<RepositoryBean> findByOrganizationNameAndRepositoryName(String organizationName, String repositoryName) throws IOException {
		
		List<RepositoryBean> items = repositoryDbRepository.findByOrganizationNameAndRepositoryName(organizationName, repositoryName);
		
		for (RepositoryBean item : items) {
			item.setBranchList(new ProxyBranchList(gitSource, item));
		}
		
		return items;
	}
	
	public RepositoryBean saveAndFlush(RepositoryBean repository) {
		return repositoryDbRepository.saveAndFlush(repository);
	}
	
//	public Collection<BranchBean> getBranches(RepositoryBean repository) throws IOException, GitAPIException {
//		return repositoryGitDAO.getBranches(repository);
//	}
//	
//	public List<CommitBean> getLog(RepositoryBean repository, BranchBean branch, Integer maxCount, Integer skip) throws IOException, GitAPIException {
//		return repositoryGitDAO.getLog(repository, branch, maxCount, skip);
//	}
	
	private class ProxyBranchList implements RepositoryBean.BranchList {

		private RealBranchList branchList = null;

		private GitSource gitSource;
		private RepositoryBean repository;
		
		public ProxyBranchList(GitSource gitSource, RepositoryBean repository) {
			this.gitSource = gitSource;
			this.repository = repository;
		}
		
		@Override
		public Collection<BranchBean> getBranches() throws IOException, GitAPIException {
			
			if (branchList == null) {
				branchList = new RealBranchList(gitSource, repository);
			}
			
			return branchList.getBranches();
		}
	}
	
	private class RealBranchList implements RepositoryBean.BranchList {

		/*
		 * Technically we cannot @Autowired "gitSource" in here
		 * (because Spring @Component doesn't accept a non-trivial
		 * constructor with >=1 arguments. So we get gitSource from
		 * repository/DAO and pass the information all the way done
		 * to here.
		 */
		private GitSource gitSource;
		private RepositoryBean repository;
		
		public RealBranchList(GitSource gitSource, RepositoryBean repository) {
			this.gitSource = gitSource;
			this.repository = repository;
		}
		
		@Override
		public Collection<BranchBean> getBranches() throws IOException, GitAPIException {
			
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
	}
}
