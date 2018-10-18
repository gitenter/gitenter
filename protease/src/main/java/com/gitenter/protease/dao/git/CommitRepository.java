package com.gitenter.protease.dao.git;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.eclipse.jgit.api.errors.GitAPIException;

import com.gitenter.protease.domain.git.CommitBean;

public interface CommitRepository {

	public Optional<CommitBean> findById(Integer id) throws IOException, GitAPIException;
	public List<CommitBean> findByRepositoryIdAndCommitSha(Integer repositoryId, String commitSha) throws IOException, GitAPIException;
	public List<CommitBean> findByRepositoryIdAndCommitShaIn(Integer repositoryId, List<String> commitShas)  throws IOException, GitAPIException;
	
	public void deleteById(Integer id);
	public CommitBean saveAndFlush(CommitBean commit);
}
