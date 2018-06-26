package com.gitenter.dao.git;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.eclipse.jgit.api.errors.GitAPIException;

import com.gitenter.domain.git.BranchBean;
import com.gitenter.domain.git.CommitBean;

public interface CommitRepository {

	public Optional<CommitBean> findById(Integer id) throws IOException, GitAPIException;
	public List<CommitBean> findByRepositoryIdAndCommitSha(Integer repositoryId, String commitSha) throws IOException, GitAPIException;
	public List<CommitBean> findByRepositoryIdAndCommitShaIn(Integer repositoryId, List<String> commitShas)  throws IOException, GitAPIException;
	
	public CommitBean saveAndFlush(CommitBean commit);
}
