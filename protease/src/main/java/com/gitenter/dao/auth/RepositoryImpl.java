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
import com.gitenter.domain.git.TagBean;
import com.gitenter.gitar.GitBareRepository;
import com.gitenter.gitar.GitBranch;
import com.gitenter.gitar.GitCommit;
import com.gitenter.gitar.GitRepository;
import com.gitenter.gitar.GitTag;
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
	
	public RepositoryBean saveAndFlush(RepositoryBean repository) {
		return repositoryDatabaseRepository.saveAndFlush(repository);
	}
	
	/*
	 * Technically we cannot @Autowired "gitSource" in the inner 
	 * classes such as the placeholders, because Spring @Component 
	 * doesn't accept a non-trivial constructor with >=1 arguments. 
	 * 
	 * So we want the outer class to @Autowired gitSource, and
	 * pass the related information down to the real git access
	 * operations all in the scope of this outer class (with the
	 * usage of this helper method).
	 */
	private GitRepository getGitRepository (RepositoryBean repository) throws IOException, GitAPIException {
		
		File repositoryDirectory = gitSource.getBareRepositoryDirectory(
				repository.getOrganization().getName(), 
				repository.getName());
		
		return GitBareRepository.getInstance(repositoryDirectory);
	}
	
	private void updatePlaceholders(RepositoryBean item) {
		item.setBranchPlaceholder(new BranchPlaceholderImpl(item));
		item.setBranchesPlaceholder(new ProxyBranchesPlaceholder(item));
		item.setTagPlaceholder(new TagPlaceholderImpl(item));
		item.setTagsPlaceholder(new ProxyTagsPlaceholder(item));
	}
	
	private BranchBean getBranchBean(RepositoryBean repository, String branchName) {
		
		BranchBean branch = new BranchBean(branchName, repository);
		branch.setHeadPlaceholder(new ProxyHeadPlaceholder(branch));
		branch.setLogPlaceholder(new LogPlaceholderImpl(repository, branchName));
		
		return branch;
	}
	
	private TagBean getTagBean(RepositoryBean repository, String tagName) {
		
		TagBean tag = new TagBean(tagName, repository);
		tag.setCommitPlaceholder(new ProxyCommitPlaceholder(tag));
		
		return tag;
	}
	
	private interface Placeholder<T> {
		public T get() throws IOException, GitAPIException;
	}
	
	abstract private class ProxyPlaceholder<T,K> implements Placeholder<T> {
		
		protected K anchor;
		private T proxyValue;
		
		public ProxyPlaceholder(K anchor) {
			this.anchor = anchor;
		}
		
		@Override
		public T get() throws IOException, GitAPIException {
			
			if (proxyValue == null) {
				proxyValue = getReal();
			}
			return proxyValue;
		}
		
		abstract protected T getReal() throws IOException, GitAPIException;
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
		public BranchBean get(String branchName) {
			return getBranchBean(repository, branchName);
		}
	}
	
	private class ProxyBranchesPlaceholder extends ProxyPlaceholder<Collection<BranchBean>,RepositoryBean> implements RepositoryBean.BranchesPlaceholder {
		
		public ProxyBranchesPlaceholder(RepositoryBean repository) {
			super(repository);
		}

		@Override
		protected Collection<BranchBean> getReal() throws IOException, GitAPIException {
			
			GitRepository gitRepository = getGitRepository(anchor);
			Collection<GitBranch> gitBranches = gitRepository.getBranches();
			
			Collection<BranchBean> branches = new ArrayList<BranchBean>();
			for (GitBranch gitBranch : gitBranches) {
				branches.add(getBranchBean(anchor, gitBranch.getName()));
			}
			
			return branches;
		}
	}
	
	private class ProxyHeadPlaceholder extends ProxyPlaceholder<CommitBean,BranchBean> implements BranchBean.HeadPlaceholder {

		public ProxyHeadPlaceholder(BranchBean branch) {
			super(branch);
		}
		
		@Override
		public CommitBean getReal() throws IOException, GitAPIException {
			
			GitRepository gitRepository = getGitRepository(anchor.getRepository());
			GitCommit gitCommit = gitRepository.getBranch(anchor.getName()).getHead();
			
			CommitBean commit = commitDatabaseRepository.findByRepositoryIdAndSha(anchor.getRepository().getId(), gitCommit.getSha()).get(0);
			commit.updateFromGitCommit(gitCommit);
			
			return commit;
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
		public List<CommitBean> get(Integer maxCount, Integer skip) throws IOException, GitAPIException {
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
	
	private class TagPlaceholderImpl implements RepositoryBean.TagPlaceholder {

		private RepositoryBean repository;
		
		public TagPlaceholderImpl(RepositoryBean repository) {
			this.repository = repository;
		}
		
		@Override
		public TagBean get(String tagName) {
			return getTagBean(repository, tagName);
		}
	}
	
	private class ProxyTagsPlaceholder extends ProxyPlaceholder<Collection<TagBean>,RepositoryBean> implements RepositoryBean.TagsPlaceholder {
		
		public ProxyTagsPlaceholder(RepositoryBean repository) {
			super(repository);
		}
		
		@Override
		public Collection<TagBean> getReal() throws IOException, GitAPIException {
			
			GitRepository gitRepository = getGitRepository(anchor);
			Collection<GitTag> gitTags = gitRepository.getTags();

			Collection<TagBean> tags = new ArrayList<TagBean>();
			for (GitTag gitTag : gitTags) {
				tags.add(getTagBean(anchor, gitTag.getName()));
			}
			
			return tags;
		}
	}
	
	private class ProxyCommitPlaceholder extends ProxyPlaceholder<CommitBean,TagBean> implements TagBean.CommitPlaceholder {

		public ProxyCommitPlaceholder(TagBean tag) {
			super(tag);
		}
		
		@Override
		public CommitBean getReal() throws IOException, GitAPIException {
			
			GitRepository gitRepository = getGitRepository(anchor.getRepository());
			GitCommit gitCommit = gitRepository.getTag(anchor.getName()).getCommit();
			
			CommitBean commit = commitDatabaseRepository.findByRepositoryIdAndSha(anchor.getRepository().getId(), gitCommit.getSha()).get(0);
			commit.updateFromGitCommit(gitCommit);
			
			return commit;
		}
	}
}
