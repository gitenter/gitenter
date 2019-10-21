package com.gitenter.protease.dao.git;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.gitenter.protease.domain.git.IncludeFileBean;

@Repository
class IncludeFileRepositoryImpl implements IncludeFileRepository {

	@Autowired private IncludeFileDatabaseRepository includeFileDatabaseRepository;
	@Autowired private IncludeFileGitUpdateFactory includeFileGitUpdateFactory;

	public Optional<IncludeFileBean> findById(Integer id) throws IOException, GitAPIException {
	
	Optional<IncludeFileBean> items = includeFileDatabaseRepository.findById(id);
	
	if (items.isPresent()) {
		IncludeFileBean item = items.get();
		includeFileGitUpdateFactory.update(item);
	}

	return items;
}

	public List<IncludeFileBean> findByCommitIdAndRelativePath(Integer commitId, String relativePath) throws IOException, GitAPIException {

		List<IncludeFileBean> items = includeFileDatabaseRepository.findByCommitIdAndRelativePath(commitId, relativePath);	
		
		for (IncludeFileBean item : items) {
			includeFileGitUpdateFactory.update(item);
		}
		
		return items;
	}
	
	public List<IncludeFileBean> findByCommitIdAndRelativePathIn(Integer commitId, List<String> relativePaths) throws IOException, GitAPIException {
	
		List<IncludeFileBean> items = includeFileDatabaseRepository.findByCommitIdAndRelativePathIn(commitId, relativePaths);
		
		for (IncludeFileBean item : items) {
			includeFileGitUpdateFactory.update(item);
		}
		
		return items;
	}
	
	public List<IncludeFileBean> findByCommitShaAndRelativePath(String commitSha, String relativePath) throws IOException, GitAPIException {

		List<IncludeFileBean> items = includeFileDatabaseRepository.findByShaAndRelativePath(commitSha, relativePath);
		
		for (IncludeFileBean item : items) {
			includeFileGitUpdateFactory.update(item);
		}
		
		return items; 
	}
	
	public IncludeFileBean saveAndFlush(IncludeFileBean document) {
		return includeFileDatabaseRepository.saveAndFlush(document);
	}
}
