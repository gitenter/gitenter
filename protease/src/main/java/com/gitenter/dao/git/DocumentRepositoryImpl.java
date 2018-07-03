package com.gitenter.dao.git;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.gitenter.domain.git.DocumentBean;
import com.gitenter.gitar.GitBareRepository;
import com.gitenter.gitar.GitCommit;
import com.gitenter.gitar.GitFile;
import com.gitenter.gitar.GitRepository;
import com.gitenter.protease.source.GitSource;

@Repository
class DocumentRepositoryImpl implements DocumentRepository {

	@Autowired private DocumentDatabaseRepository databaseRepository;
	@Autowired private GitSource gitSource;

	public Optional<DocumentBean> findById(Integer id) throws IOException, GitAPIException {
	
	Optional<DocumentBean> items = databaseRepository.findById(id);
	
	if (items.isPresent()) {
		DocumentBean item = items.get();
		updateFromGit(item);
	}

	return items;
}

	public List<DocumentBean> findByCommitIdAndRelativePath(Integer commitId, String relativePath) throws IOException, GitAPIException {

		List<DocumentBean> items = databaseRepository.findByCommitIdAndRelativePath(commitId, relativePath);	
		
		for (DocumentBean item : items) {
			updateFromGit(item);
		}
		
		return items;
	}
	
	public List<DocumentBean> findByCommitIdAndRelativePathIn(Integer commitId, List<String> relativePaths) throws IOException, GitAPIException {
	
		List<DocumentBean> items = databaseRepository.findByCommitIdAndRelativePathIn(commitId, relativePaths);
		
		for (DocumentBean item : items) {
			updateFromGit(item);
		}
		
		return items;
	}
	
	public List<DocumentBean> findByCommitShaAndRelativePath(String commitSha, String relativePath) throws IOException, GitAPIException {

		List<DocumentBean> items = databaseRepository.findByShaAndRelativePath(commitSha, relativePath);
		
		for (DocumentBean item : items) {
			updateFromGit(item);
		}
		
		return items; 
	}
	
	private void updateFromGit(DocumentBean document) throws IOException, GitAPIException {

		File repositoryDirectory = gitSource.getBareRepositoryDirectory(
				document.getCommit().getRepository().getOrganization().getName(), 
				document.getCommit().getRepository().getName());
		
		GitRepository gitRepository = GitBareRepository.getInstance(repositoryDirectory);
		GitCommit gitCommit = gitRepository.getCommit(document.getCommit().getSha());
		GitFile gitFile = gitCommit.getFile(document.getRelativePath());
		
		assert document.getRelativePath().equals(gitFile.getRelativePath());
		document.setFromGit(gitFile);

		/*
		 * TODO:
		 * But validCommit placeholders is not setup yet.
		 */
		assert document.getCommit().getSha().equals(gitCommit.getSha());
		document.getCommit().setFromGit(gitCommit);
	}
	
	public DocumentBean saveAndFlush(DocumentBean document) {
		return databaseRepository.saveAndFlush(document);
	}
}
