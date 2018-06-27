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
import com.gitenter.gitar.GitFilepath;
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
		
		commit.updateFromGitCommit(gitCommit);
		
		if (commit instanceof CommitValidBean) {
			CommitValidBean validCommit = (CommitValidBean)commit;
			validCommit.setRootPlaceholder(new ProxyRootPlaceholder(validCommit, gitCommit));
		}
	}
	
	public CommitBean saveAndFlush(CommitBean commit) {
		return commitDatabaseRepository.saveAndFlush(commit);
	}
	
	private static class ProxyRootPlaceholder extends GitProxyPlaceholder<FolderBean> implements CommitValidBean.RootPlaceholder {

		final private CommitBean commit;
		final private GitCommit gitCommit;
		
		protected ProxyRootPlaceholder(CommitBean commit, GitCommit gitCommit) {
			this.commit = commit;
			this.gitCommit = gitCommit;
		}

		@Override
		protected FolderBean getReal() throws IOException, GitAPIException {
			GitFolder gitFolder = gitCommit.getRoot();
			return getFolderBean(gitFolder, commit);
		}
	}
	
	private static FileBean getFileBean(GitFilepath gitFilepath, CommitBean commit) {
		
		FileBean file = new FileBean();
		file.setRelativePath(gitFilepath.getRelativePath());
		file.setCommit(commit);
		file.setBlobContentPlaceholder(new ProxyBlobContentPlaceholder(gitFilepath));
		
		return file;
	}
	
	private static FolderBean getFolderBean(GitFolder gitFolder, CommitBean commit) {
		
		FolderBean folder = new FolderBean();
		folder.setRelativePath(gitFolder.getRelativePath());
		folder.setCommit(commit);
		
		Collection<PathBean> subpath = new ArrayList<PathBean>();
		for (GitPath path : gitFolder.list()) {
			if (path instanceof GitFolder) {
				subpath.add(getFolderBean((GitFolder)path, commit));
			}
			else {
				assert path instanceof GitFilepath;
				subpath.add(getFileBean((GitFilepath)path, commit));
			}
			folder.setSubpath(subpath);
		}
		
		return folder;
	}
	
	private static class ProxyBlobContentPlaceholder extends GitProxyPlaceholder<byte[]> implements FileBean.BlobContentPlaceholder {

		final private GitFilepath gitFilepath;
		
		private ProxyBlobContentPlaceholder(GitFilepath gitFilepath) {
			this.gitFilepath = gitFilepath;
		}

		@Override
		protected byte[] getReal() throws IOException, GitAPIException {
			/*
			 * TODO:
			 * Should either let gitFilepath not doing down casting (can use proxy to get 
			 * the data), or public "downCasting()"
			 */
//			GitFile gitFile = gitCommit.getFile()
			return null;
		}
	}
}
