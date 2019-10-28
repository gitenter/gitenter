package com.gitenter.protease.dao.git;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.eclipse.jgit.api.errors.GitAPIException;

import com.gitenter.protease.domain.git.IncludeFileBean;

public interface IncludeFileRepository {

	public Optional<IncludeFileBean> findById(Integer id) throws IOException, GitAPIException;
	public List<IncludeFileBean> findByCommitIdAndRelativePath(Integer commitId, String relativePath) throws IOException, GitAPIException;
	public List<IncludeFileBean> findByCommitShaAndRelativePath(String commitSha, String relativePath) throws IOException, GitAPIException;
	public List<IncludeFileBean> findByCommitIdAndRelativePathIn(Integer commitId, List<String> relativePaths) throws IOException, GitAPIException;

	public IncludeFileBean saveAndFlush(IncludeFileBean document);
}
