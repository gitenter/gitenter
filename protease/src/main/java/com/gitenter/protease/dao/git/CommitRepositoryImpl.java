package com.gitenter.protease.dao.git;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.gitenter.gitar.GitBareRepository;
import com.gitenter.gitar.GitCommit;
import com.gitenter.gitar.GitHistoricalFile;
import com.gitenter.gitar.GitHistoricalFolder;
import com.gitenter.gitar.GitHistoricalPath;
import com.gitenter.gitar.GitRepository;
import com.gitenter.gitar.util.GitProxyPlaceholder;
import com.gitenter.protease.domain.git.CommitBean;
import com.gitenter.protease.domain.git.FileBean;
import com.gitenter.protease.domain.git.FolderBean;
import com.gitenter.protease.domain.git.PathBean;
import com.gitenter.protease.domain.git.ValidCommitBean;
import com.gitenter.protease.source.GitSource;

@Repository
public class CommitRepositoryImpl implements CommitRepository {

	@Autowired private CommitDatabaseRepository databaseRepository;
	@Autowired private GitSource gitSource;
	
	public Optional<CommitBean> findById(Integer id) throws IOException, GitAPIException {
		
		Optional<CommitBean> items = databaseRepository.findById(id);
		
		if (items.isPresent()) {
			CommitBean item = items.get();
			updateFromGit(item);
		}
		
		return items;
	}
	
	public List<CommitBean> findByRepositoryIdAndCommitSha(Integer repositoryId, String commitSha) throws IOException, GitAPIException {
		
		List<CommitBean> items = databaseRepository.findByRepositoryIdAndSha(repositoryId, commitSha);
		
		for (CommitBean item : items) {
			updateFromGit(item);
		}
		
		return items;
	}
	
	public List<CommitBean> findByRepositoryIdAndCommitShaIn(Integer repositoryId, List<String> commitShas) throws IOException, GitAPIException {
		
		List<CommitBean> items = databaseRepository.findByRepositoryIdAndShaIn(repositoryId, commitShas);
		
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
	
	private void updateFromGit(CommitBean commit) throws IOException, GitAPIException {
		
		File repositoryDirectory = gitSource.getBareRepositoryDirectory(
				commit.getRepository().getOrganization().getName(), 
				commit.getRepository().getName());
		
		GitRepository gitRepository = GitBareRepository.getInstance(repositoryDirectory);
		GitCommit gitCommit = gitRepository.getCommit(commit.getSha());
		
		commit.setFromGitCommit(gitCommit);
		
		if (commit instanceof ValidCommitBean) {
			ValidCommitBean validCommit = (ValidCommitBean)commit;
			validCommit.setRootPlaceholder(new ProxyRootPlaceholder(validCommit, gitCommit));
			validCommit.setFilePlaceholder(new FilePlaceholderImpl(validCommit, gitCommit));
		}
	}
	
	public void deleteById(Integer id) {
		databaseRepository.deleteById(id);
	}
	
	public CommitBean saveAndFlush(CommitBean commit) {
		return databaseRepository.saveAndFlush(commit);
	}
	
	private static class ProxyRootPlaceholder extends GitProxyPlaceholder<FolderBean> implements ValidCommitBean.RootPlaceholder {

		final private ValidCommitBean commit;
		final private GitCommit gitCommit;
		
		protected ProxyRootPlaceholder(ValidCommitBean commit, GitCommit gitCommit) {
			this.commit = commit;
			this.gitCommit = gitCommit;
		}

		@Override
		protected FolderBean getReal() throws IOException, GitAPIException {
			GitHistoricalFolder gitFolder = gitCommit.getRoot();
			return getFolderBean(gitFolder, commit);
		}
	}
	
	private static class FilePlaceholderImpl implements ValidCommitBean.FilePlaceholder {

		final private ValidCommitBean commit;
		final private GitCommit gitCommit;
		
		protected FilePlaceholderImpl(ValidCommitBean commit, GitCommit gitCommit) {
			this.commit = commit;
			this.gitCommit = gitCommit;
		}
		
		@Override
		public FileBean get(String relativePath) throws IOException, GitAPIException {
			
			GitHistoricalFile gitFile = gitCommit.getFile(relativePath);
			return getFileBean(gitFile, commit);
		}
		
	}
	
	private static FileBean getFileBean(GitHistoricalFile gitFile, ValidCommitBean commit) {
		
		FileBean file = new FileBean();
		file.setFromGit(gitFile);
		file.setCommit(commit);
		
		return file;
	}
	
	private static FolderBean getFolderBean(GitHistoricalFolder gitFolder, ValidCommitBean commit) {
		
		FolderBean folder = new FolderBean();
		folder.setFromGit(gitFolder);
		folder.setCommit(commit);
		
		Collection<PathBean> subpath = new ArrayList<PathBean>();
		for (GitHistoricalPath path : gitFolder.ls()) {
			if (path instanceof GitHistoricalFolder) {
				subpath.add(getFolderBean((GitHistoricalFolder)path, commit));
			}
			else {
				assert path instanceof GitHistoricalFile;
				subpath.add(getFileBean((GitHistoricalFile)path, commit));
			}
			folder.setSubpath(subpath);
		}
		
		return folder;
	}
}
