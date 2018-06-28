package com.gitenter.dao.git;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.gitenter.domain.git.CommitBean;
import com.gitenter.domain.git.CommitValidBean;
import com.gitenter.domain.git.FileBean;
import com.gitenter.domain.git.FolderBean;
import com.gitenter.domain.git.PathBean;
import com.gitenter.gitar.GitBareRepository;
import com.gitenter.gitar.GitCommit;
import com.gitenter.gitar.GitFile;
import com.gitenter.gitar.GitFolder;
import com.gitenter.gitar.GitPath;
import com.gitenter.gitar.GitRepository;
import com.gitenter.gitar.util.GitProxyPlaceholder;
import com.gitenter.protease.source.GitSource;

@Repository
public class CommitImpl implements CommitRepository {

	@Autowired private CommitDatabaseRepository commitDatabaseRepository;
	@Autowired private GitSource gitSource;
	
	public Optional<CommitBean> findById(Integer id) throws IOException, GitAPIException {
		
		Optional<CommitBean> items = commitDatabaseRepository.findById(id);
		
		if (items.isPresent()) {
			CommitBean item = items.get();
			updateFromGit(item);
		}
		
		return items;
	}
	
	public List<CommitBean> findByRepositoryIdAndCommitSha(Integer repositoryId, String commitSha) throws IOException, GitAPIException {
		
		List<CommitBean> items = commitDatabaseRepository.findByRepositoryIdAndSha(repositoryId, commitSha);
		
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
		
		List<CommitBean> items = commitDatabaseRepository.findByRepositoryIdAndShaIn(repositoryId, commitShas);
		
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
		
		commit.setFromGit(gitCommit);
		
		if (commit instanceof CommitValidBean) {
			CommitValidBean validCommit = (CommitValidBean)commit;
			validCommit.setRootPlaceholder(new ProxyRootPlaceholder(validCommit, gitCommit));
			validCommit.setFilePlaceholder(new FilePlaceholderImpl(validCommit, gitCommit));
		}
	}
	
	public CommitBean saveAndFlush(CommitBean commit) {
		return commitDatabaseRepository.saveAndFlush(commit);
	}
	
	private static class ProxyRootPlaceholder extends GitProxyPlaceholder<FolderBean> implements CommitValidBean.RootPlaceholder {

		final private CommitValidBean commit;
		final private GitCommit gitCommit;
		
		protected ProxyRootPlaceholder(CommitValidBean commit, GitCommit gitCommit) {
			this.commit = commit;
			this.gitCommit = gitCommit;
		}

		@Override
		protected FolderBean getReal() throws IOException, GitAPIException {
			GitFolder gitFolder = gitCommit.getRoot();
			return getFolderBean(gitFolder, commit);
		}
	}
	
	private static class FilePlaceholderImpl implements CommitValidBean.FilePlaceholder {

		final private CommitValidBean commit;
		final private GitCommit gitCommit;
		
		protected FilePlaceholderImpl(CommitValidBean commit, GitCommit gitCommit) {
			this.commit = commit;
			this.gitCommit = gitCommit;
		}
		
		@Override
		public FileBean get(String relativePath) throws IOException, GitAPIException {
			
			GitFile gitFile = gitCommit.getFile(relativePath);
			return getFileBean(gitFile, commit);
		}
		
	}
	
	private static FileBean getFileBean(GitFile gitFile, CommitValidBean commit) {
		
		FileBean file = new FileBean();
		file.setFromGit(gitFile);
		file.setCommit(commit);
		file.setBlobContentPlaceholder(new ProxyBlobContentPlaceholder(gitFile));
		
		return file;
	}
	
	private static FolderBean getFolderBean(GitFolder gitFolder, CommitValidBean commit) {
		
		FolderBean folder = new FolderBean();
		folder.setFromGit(gitFolder);
		folder.setCommit(commit);
		
		Collection<PathBean> subpath = new ArrayList<PathBean>();
		for (GitPath path : gitFolder.list()) {
			if (path instanceof GitFolder) {
				subpath.add(getFolderBean((GitFolder)path, commit));
			}
			else {
				assert path instanceof GitFile;
				subpath.add(getFileBean((GitFile)path, commit));
			}
			folder.setSubpath(subpath);
		}
		
		return folder;
	}
	
	/*
	 * TODO:
	 * Used by here and "DocumentImpl", but it seems to put it in either place is not
	 * a good idea. Check where is a better place to put it.
	 */
	static class ProxyBlobContentPlaceholder extends GitProxyPlaceholder<byte[]> implements FileBean.BlobContentPlaceholder {

		final private GitFile gitFile;
		
		ProxyBlobContentPlaceholder(GitFile gitFile) {
			this.gitFile = gitFile;
		}

		@Override
		protected byte[] getReal() throws IOException, GitAPIException {
			return gitFile.getBlobContent();
		}
	}
}
