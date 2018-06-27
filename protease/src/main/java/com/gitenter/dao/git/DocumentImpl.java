package com.gitenter.dao.git;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.gitenter.dao.auth.RepositoryRepository;
import com.gitenter.domain.git.BranchBean;
import com.gitenter.domain.git.CommitBean;
import com.gitenter.domain.git.DocumentBean;
import com.gitenter.domain.git.FileBean;
import com.gitenter.gitar.*;
import com.gitenter.gitar.util.GitProxyPlaceholder;
import com.gitenter.protease.source.GitSource;

@Repository
class DocumentImpl implements DocumentRepository {

	@Autowired private DocumentDatabaseRepository documentDatabaseRepository;
	@Autowired private GitSource gitSource;

	public Optional<DocumentBean> findById(Integer id) throws IOException, GitAPIException {
	
	Optional<DocumentBean> items = documentDatabaseRepository.findById(id);
	
	if (items.isPresent()) {
		DocumentBean item = items.get();
		updateFromGit(item);
	}

	return items;
}

	public List<DocumentBean> findByCommitIdAndRelativePath(Integer commitId, String relativePath) throws IOException, GitAPIException {

		List<DocumentBean> items = documentDatabaseRepository.findByCommitIdAndRelativePath(commitId, relativePath);	
		
		for (DocumentBean item : items) {
			updateFromGit(item);
		}
		
		return items;
	}
	
	public List<DocumentBean> findByCommitIdAndRelativePathIn(Integer commitId, List<String> relativePaths) throws IOException, GitAPIException {
	
		List<DocumentBean> items = documentDatabaseRepository.findByCommitIdAndRelativePathIn(commitId, relativePaths);
		
		for (DocumentBean item : items) {
			updateFromGit(item);
		}
		
		return items;
	}
	
	public List<DocumentBean> findByCommitShaAndRelativePath(String commitSha, String relativePath) throws IOException, GitAPIException {

		List<DocumentBean> items = documentDatabaseRepository.findByShaAndRelativePath(commitSha, relativePath);
		
		for (DocumentBean item : items) {
			updateFromGit(item);
		}
		
		return items; 
	}
	
//	public DocumentBean findByRepositoryIdAndBranchAndRelativeFilepath(Integer repositoryId, BranchBean branch, String relativeFilepath) throws IOException, GitAPIException {
//		
//		/*
//		 * TODO:
//		 * Should be a better way rather than query the database twice?
//		 * It is pretty hard, since "branch" is not saved in database.
//		 */
//		CommitBean commit = branch.getHead();
//		return findByCommitIdAndRelativeFilepath(commit.getId(), relativeFilepath);
//	}
	
	private void updateFromGit(DocumentBean document) throws IOException, GitAPIException {

		File repositoryDirectory = gitSource.getBareRepositoryDirectory(
				document.getCommit().getRepository().getOrganization().getName(), 
				document.getCommit().getRepository().getName());
		
		GitRepository gitRepository = GitBareRepository.getInstance(repositoryDirectory);
		GitCommit gitCommit = gitRepository.getCommit(document.getCommit().getSha());
		GitFile gitFile = gitCommit.getFile(document.getRelativePath());
		
		document.setName(gitFile.getName());
		assert document.getRelativePath().equals(gitFile.getRelativePath());
		assert document.getCommit().getSha().equals(gitCommit.getSha());
		document.setBlobContentPlaceholder(new ProxyBlobContentPlaceholder(gitFile));
	}
	
	public DocumentBean saveAndFlush(DocumentBean document) {
		
		return documentDatabaseRepository.saveAndFlush(document);
	}
	
	/*
	 * TODO:
	 * Used by here and "CommitImpl", but it seems to put it in either place is not
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
