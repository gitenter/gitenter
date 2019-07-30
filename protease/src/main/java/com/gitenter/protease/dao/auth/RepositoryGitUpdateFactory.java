package com.gitenter.protease.dao.auth;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.gitenter.gitar.GitBareRepository;
import com.gitenter.gitar.GitBranch;
import com.gitenter.gitar.GitCommit;
import com.gitenter.gitar.GitRepository;
import com.gitenter.gitar.GitTag;
import com.gitenter.gitar.util.GitProxyPlaceholder;
import com.gitenter.protease.config.bean.GitSource;
import com.gitenter.protease.dao.GitUpdateFactory;
import com.gitenter.protease.dao.exception.BranchNotExistException;
import com.gitenter.protease.dao.git.CommitDatabaseRepository;
import com.gitenter.protease.domain.auth.RepositoryBean;
import com.gitenter.protease.domain.git.BranchBean;
import com.gitenter.protease.domain.git.CommitBean;
import com.gitenter.protease.domain.git.TagBean;

@Component
public class RepositoryGitUpdateFactory implements GitUpdateFactory<RepositoryBean> {
	
	@Autowired private CommitDatabaseRepository commitDatabaseRepository;
	@Autowired private GitSource gitSource;

	/*
	 * TODO:
	 * Move this boilerplate to bean definition. Maybe by an annotation
	 * @Proxy or @Lazy and it will wrap the correct placeholder implementation.
	 */
	public void update(RepositoryBean item) {
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

		private final RepositoryBean repository;
		
		private BranchPlaceholderImpl(RepositoryBean repository) {
			this.repository = repository;
		}
		
		@Override
		public BranchBean get(String branchName) {
			return getBranchBean(repository, branchName);
		}
	}
	
	private class ProxyBranchesPlaceholder extends GitProxyPlaceholder<Collection<BranchBean>> implements RepositoryBean.BranchesPlaceholder {
		
		private final RepositoryBean repository;
		
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

		private final BranchBean branch;
		
		private ProxyHeadPlaceholder(BranchBean branch) {
			this.branch = branch;
		}
		
		@Override
		protected CommitBean getReal() throws IOException, GitAPIException {
			
			GitBranch gitBranch = getGitBranchFromBranch(branch);
			GitCommit gitCommit = gitBranch.getHead();
			
			CommitBean commit = commitDatabaseRepository.findByRepositoryIdAndSha(branch.getRepository().getId(), gitCommit.getSha()).get(0);
			commit.setFromGitCommit(gitCommit);
			
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

		private final BranchBean branch;
		
		private LogPlaceholderImpl(BranchBean branch) {
			this.branch = branch;
		}
		
		@Override
		public List<CommitBean> getInDatabase(Integer maxCount, Integer skip) throws IOException, GitAPIException {
			GitBranch gitBranch = getGitBranchFromBranch(branch);
			List<GitCommit> gitLog = gitBranch.getLog(maxCount, skip);
			return gitLog2FillValuedInDbCommit(gitLog);	
		}
		
		@Override
		public List<CommitBean> getInDatabase(String oldSha, String newSha) throws IOException, GitAPIException {
			GitBranch gitBranch = getGitBranchFromBranch(branch);
			List<GitCommit> gitLog = gitBranch.getLog(oldSha, newSha);
			return gitLog2FillValuedInDbCommit(gitLog);	
		}
		
		private List<CommitBean> gitLog2FillValuedInDbCommit(List<GitCommit> gitLog) {
			LinkedHashMap<String,GitCommit> logMap = getLogMap(gitLog);
			List<CommitBean> inDbCommits = checkInDbCommits(logMap);
			for (CommitBean commit : inDbCommits) {
				commit.setFromGitCommit(logMap.get(commit.getSha()));
			}
			
			return inDbCommits;
		}
		
		@Override
		public List<GitCommit> getUnsaved(Integer maxCount, Integer skip) throws IOException, GitAPIException {
			GitBranch gitBranch = getGitBranchFromBranch(branch);
			List<GitCommit> gitLog = gitBranch.getLog(maxCount, skip);
			return updateByRemoveSaved(gitLog);
		}

		@Override
		public List<GitCommit> getUnsaved(String oldSha, String newSha) throws IOException, GitAPIException {
			GitBranch gitBranch = getGitBranchFromBranch(branch);
			List<GitCommit> gitLog = gitBranch.getLog(oldSha, newSha);
			return updateByRemoveSaved(gitLog);
		}
		
		private List<GitCommit> updateByRemoveSaved(List<GitCommit> gitLog) {
			LinkedHashMap<String,GitCommit> logMap = getLogMap(gitLog);
			List<CommitBean> inDbCommits = checkInDbCommits(logMap);
			
			/*
			 * Actually when the log comes (as the reverse time order), the
			 * in database ones are always the last several of them. That may
			 * help to improve the performance, as we don't need to iterate the
			 * entire set and move out commits individually. However, since the
			 * most time consuming part is the database query parts (to see what
			 * commits already exists), it probably doesn't make a big difference
			 * to make optimization in here.
			 */
			for (CommitBean commit : inDbCommits) {
				logMap.remove(commit.getSha());
			}
			
			/*
			 * TODO:
			 * Need to double check whether it indeed keep orders.
			 */
			return new ArrayList<>(logMap.values());
		}
		
		private LinkedHashMap<String,GitCommit> getLogMap(List<GitCommit> gitLog) {
			/*
			 * Keep insert order. So it should maintain the revert time order which
			 * `git log` command provides.
			 */
			LinkedHashMap<String,GitCommit> logMap = new LinkedHashMap<String,GitCommit>();
			for (GitCommit gitCommit : gitLog) {
				logMap.put(gitCommit.getSha(), gitCommit);
			}
			
			return logMap;
		}
			
		private List<CommitBean> checkInDbCommits(LinkedHashMap<String,GitCommit> logMap) { 
			
			/*
			 * TODO:
			 * Need to double check whether it indeed keep orders.
			 */
			List<String> shas = new ArrayList<>(logMap.keySet());
			
			/*
			 * Do it in one single SQL query by performance concerns.
			 * Also, use directory database query so git information is not
			 * automatically included.
			 * 
			 * TODO:
			 * But this only include the in database ones. 
			 */
			List<CommitBean> inDbCommits = commitDatabaseRepository.findByRepositoryIdAndShaIn(branch.getRepository().getId(), shas);
			return inDbCommits;
		}
	}
	
	private class TagPlaceholderImpl implements RepositoryBean.TagPlaceholder {

		private final RepositoryBean repository;
		
		private TagPlaceholderImpl(RepositoryBean repository) {
			this.repository = repository;
		}
		
		@Override
		public TagBean get(String tagName) {
			return getTagBean(repository, tagName);
		}
	}
	
	private class ProxyTagsPlaceholder extends GitProxyPlaceholder<Collection<TagBean>> implements RepositoryBean.TagsPlaceholder {
		
		private final RepositoryBean repository;
		
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

		private final TagBean tag;
		
		private ProxyCommitPlaceholder(TagBean tag) {
			this.tag = tag;
		}
		
		@Override
		protected CommitBean getReal() throws IOException, GitAPIException {
			
			GitRepository gitRepository = getGitRepository(tag.getRepository());
			GitCommit gitCommit = gitRepository.getTag(tag.getName()).getCommit();
			
			CommitBean commit = commitDatabaseRepository.findByRepositoryIdAndSha(tag.getRepository().getId(), gitCommit.getSha()).get(0);
			commit.setFromGitCommit(gitCommit);
			
			return commit;
		}
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
	private GitRepository getGitRepository(RepositoryBean repository) throws IOException, GitAPIException {
		
		File repositoryDirectory = gitSource.getBareRepositoryDirectory(
				repository.getOrganization().getName(), 
				repository.getName());
		
		return GitBareRepository.getInstance(repositoryDirectory);
	}
	
	private GitBranch getGitBranchFromBranch(BranchBean branch) throws IOException, GitAPIException {
		GitRepository gitRepository = getGitRepository(branch.getRepository());
		GitBranch gitBranch = gitRepository.getBranch(branch.getName());
		if (gitBranch == null) {
			throw new BranchNotExistException(branch.getName());
		}
		
		return gitBranch;
	}
	
	void delete(RepositoryBean repository) throws IOException, GitAPIException {
		GitRepository gitRepository = getGitRepository(repository);
		GitRepository.delete(gitRepository);
	}
}
