package com.gitenter.protease.dao.auth;

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

import com.gitenter.gitar.GitBareRepository;
import com.gitenter.gitar.GitBranch;
import com.gitenter.gitar.GitCommit;
import com.gitenter.gitar.GitRepository;
import com.gitenter.gitar.GitTag;
import com.gitenter.gitar.util.GitProxyPlaceholder;
import com.gitenter.protease.dao.git.CommitDatabaseRepository;
import com.gitenter.protease.domain.auth.RepositoryBean;
import com.gitenter.protease.domain.git.BranchBean;
import com.gitenter.protease.domain.git.CommitBean;
import com.gitenter.protease.domain.git.TagBean;
import com.gitenter.protease.source.GitSource;

@Repository
class RepositoryRepositoryImpl implements RepositoryRepository {

	@Autowired private RepositoryDatabaseRepository repositoryDatabaseRepository;
	@Autowired private CommitDatabaseRepository commitDatabaseRepository;
	@Autowired private GitSource gitSource;
	
	public Optional<RepositoryBean> findById(Integer id) {
		
		Optional<RepositoryBean> items = repositoryDatabaseRepository.findById(id); 
		
		if (items.isPresent()) {
			RepositoryBean item = items.get();
			updatePlaceholders(item);
		}
		
		return items;
	}
	
	public List<RepositoryBean> findByOrganizationNameAndRepositoryName(String organizationName, String repositoryName) {
		
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
	
	/*
	 * TODO:
	 * Move this boilerplate to bean definition. Maybe by an annotation
	 * @Proxy or @Lazy and it will wrap the correct placeholder implementation.
	 */
	private void updatePlaceholders(RepositoryBean item) {
		item.setBranchPlaceholder(new BranchPlaceholderImpl(item));
		item.setBranchesPlaceholder(new ProxyBranchesPlaceholder(item));
		item.setTagPlaceholder(new TagPlaceholderImpl(item));
		item.setTagsPlaceholder(new ProxyTagsPlaceholder(item));
	}
	
	private BranchBean getBranchBean(RepositoryBean repository, String branchName) {
		
		BranchBean branch = new BranchBean();
		branch.setName(branchName);
		branch.setRepository(repository);
		branch.setHeadPlaceholder(new ProxyHeadPlaceholder(branch));
		branch.setLogPlaceholder(new LogPlaceholderImpl(branch));
		
		return branch;
	}
	
	private TagBean getTagBean(RepositoryBean repository, String tagName) {
		
		TagBean tag = new TagBean();
		tag.setName(tagName);
		tag.setRepository(repository);
		tag.setCommitPlaceholder(new ProxyCommitPlaceholder(tag));
		
		return tag;
	}
	
	/*
	 * No need to use proxy pattern in here, since to execute the constructor
	 * is so cheap. The reason not implement the logic in "RepositoryBean", 
	 * is because we want to keep "RepositoryBean" to have no knowledge on
	 * "ProxyHeadPlaceholder" or "ProxyLogPlaceholder".
	 */
	private class BranchPlaceholderImpl implements RepositoryBean.BranchPlaceholder {

		final private RepositoryBean repository;
		
		private BranchPlaceholderImpl(RepositoryBean repository) {
			this.repository = repository;
		}
		
		@Override
		public BranchBean get(String branchName) {
			return getBranchBean(repository, branchName);
		}
	}
	
	private class ProxyBranchesPlaceholder extends GitProxyPlaceholder<Collection<BranchBean>> implements RepositoryBean.BranchesPlaceholder {
		
		final private RepositoryBean repository;
		
		private ProxyBranchesPlaceholder(RepositoryBean repository) {
			this.repository = repository;
		}

		@Override
		protected Collection<BranchBean> getReal() throws IOException, GitAPIException {
			
			GitRepository gitRepository = getGitRepository(repository);
			Collection<GitBranch> gitBranches = gitRepository.getBranches();
			
			Collection<BranchBean> branches = new ArrayList<BranchBean>();
			for (GitBranch gitBranch : gitBranches) {
				branches.add(getBranchBean(repository, gitBranch.getName()));
			}
			
			return branches;
		}
	}
	
	private class ProxyHeadPlaceholder extends GitProxyPlaceholder<CommitBean> implements BranchBean.HeadPlaceholder {

		final private BranchBean branch;
		
		private ProxyHeadPlaceholder(BranchBean branch) {
			this.branch = branch;
		}
		
		@Override
		protected CommitBean getReal() throws IOException, GitAPIException {
			
			GitRepository gitRepository = getGitRepository(branch.getRepository());
			GitCommit gitCommit = gitRepository.getBranch(branch.getName()).getHead();
			
			CommitBean commit = commitDatabaseRepository.findByRepositoryIdAndSha(branch.getRepository().getId(), gitCommit.getSha()).get(0);
			commit.setFromGit(gitCommit);
			
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

		final private BranchBean branch;
		
		private LogPlaceholderImpl(BranchBean branch) {
			this.branch = branch;
		}
		
		@Override
		public List<CommitBean> get(Integer maxCount, Integer skip) throws IOException, GitAPIException {
			GitRepository gitRepository = getGitRepository(branch.getRepository());
			List<GitCommit> gitLog = gitRepository.getBranch(branch.getName()).getLog();
			
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
			List<CommitBean> log = commitDatabaseRepository.findByRepositoryIdAndShaIn(branch.getRepository().getId(), shas);
			
			for (CommitBean commit : log) {
				commit.setFromGit(logMap.get(commit.getSha()));
			}
			
			return log;
		}
	}
	
	private class TagPlaceholderImpl implements RepositoryBean.TagPlaceholder {

		final private RepositoryBean repository;
		
		private TagPlaceholderImpl(RepositoryBean repository) {
			this.repository = repository;
		}
		
		@Override
		public TagBean get(String tagName) {
			return getTagBean(repository, tagName);
		}
	}
	
	private class ProxyTagsPlaceholder extends GitProxyPlaceholder<Collection<TagBean>> implements RepositoryBean.TagsPlaceholder {
		
		final private RepositoryBean repository;
		
		private ProxyTagsPlaceholder(RepositoryBean repository) {
			this.repository = repository;
		}
		
		@Override
		protected Collection<TagBean> getReal() throws IOException, GitAPIException {
			
			GitRepository gitRepository = getGitRepository(repository);
			Collection<GitTag> gitTags = gitRepository.getTags();

			Collection<TagBean> tags = new ArrayList<TagBean>();
			for (GitTag gitTag : gitTags) {
				tags.add(getTagBean(repository, gitTag.getName()));
			}
			
			return tags;
		}
	}
	
	private class ProxyCommitPlaceholder extends GitProxyPlaceholder<CommitBean> implements TagBean.CommitPlaceholder {

		final private TagBean tag;
		
		private ProxyCommitPlaceholder(TagBean tag) {
			this.tag = tag;
		}
		
		@Override
		protected CommitBean getReal() throws IOException, GitAPIException {
			
			GitRepository gitRepository = getGitRepository(tag.getRepository());
			GitCommit gitCommit = gitRepository.getTag(tag.getName()).getCommit();
			
			CommitBean commit = commitDatabaseRepository.findByRepositoryIdAndSha(tag.getRepository().getId(), gitCommit.getSha()).get(0);
			commit.setFromGit(gitCommit);
			
			return commit;
		}
	}
}
