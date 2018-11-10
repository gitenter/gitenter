package com.gitenter.protease.dao.git;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.gitenter.protease.domain.git.DocumentBean;

@Repository
class DocumentRepositoryImpl implements DocumentRepository {

	@Autowired private DocumentDatabaseRepository documentDatabaseRepository;
	@Autowired private DocumentGitUpdateFactory documentGitUpdateFactory;

	public Optional<DocumentBean> findById(Integer id) throws IOException, GitAPIException {
	
	Optional<DocumentBean> items = documentDatabaseRepository.findById(id);
	
	if (items.isPresent()) {
		DocumentBean item = items.get();
		documentGitUpdateFactory.update(item);
	}

	return items;
}

	public List<DocumentBean> findByCommitIdAndRelativePath(Integer commitId, String relativePath) throws IOException, GitAPIException {

		List<DocumentBean> items = documentDatabaseRepository.findByCommitIdAndRelativePath(commitId, relativePath);	
		
		for (DocumentBean item : items) {
			documentGitUpdateFactory.update(item);
		}
		
		return items;
	}
	
	public List<DocumentBean> findByCommitIdAndRelativePathIn(Integer commitId, List<String> relativePaths) throws IOException, GitAPIException {
	
		List<DocumentBean> items = documentDatabaseRepository.findByCommitIdAndRelativePathIn(commitId, relativePaths);
		
		for (DocumentBean item : items) {
			documentGitUpdateFactory.update(item);
		}
		
		return items;
	}
	
	public List<DocumentBean> findByCommitShaAndRelativePath(String commitSha, String relativePath) throws IOException, GitAPIException {

		List<DocumentBean> items = documentDatabaseRepository.findByShaAndRelativePath(commitSha, relativePath);
		
		for (DocumentBean item : items) {
			documentGitUpdateFactory.update(item);
		}
		
		return items; 
	}
	
	public DocumentBean saveAndFlush(DocumentBean document) {
		return documentDatabaseRepository.saveAndFlush(document);
	}
}
