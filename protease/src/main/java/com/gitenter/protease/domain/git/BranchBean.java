package com.gitenter.protease.domain.git;

import java.io.IOException;
import java.util.List;

import org.eclipse.jgit.api.errors.GitAPIException;

import com.gitenter.gitar.GitCommit;
import com.gitenter.gitar.util.GitPlaceholder;
import com.gitenter.protease.domain.auth.RepositoryBean;

import lombok.Getter;
import lombok.Setter;

public class BranchBean {
	
	@Getter
	@Setter
	private String name;
	
	@Getter
	@Setter
	private RepositoryBean repository;

	@Setter
	private HeadPlaceholder headPlaceholder;

	public CommitBean getHead() throws IOException, GitAPIException {
		return headPlaceholder.get();
	}
	
	public interface HeadPlaceholder extends GitPlaceholder<CommitBean> {
		CommitBean get() throws IOException, GitAPIException;
	}
	
	@Setter
	private LogPlaceholder logPlaceholder;
	
	public List<CommitBean> getInDatabaseLog(Integer maxCount, Integer skip) throws IOException, GitAPIException {
		return logPlaceholder.getInDatabase(maxCount, skip);
	}
	
	public List<CommitBean> getInDatabaseLog(String oldSha, String newSha) throws IOException, GitAPIException {
		return logPlaceholder.getInDatabase(oldSha, newSha);
	}
	
	/*
	 * Cannot return "CommitBean" because it is unknown yet which kind of
	 * subclass it should be.
	 */
	public List<GitCommit> getUnsavedLog(Integer maxCount, Integer skip) throws IOException, GitAPIException {
		return logPlaceholder.getUnsaved(maxCount, skip);
	}
	
	public List<GitCommit> getUnsavedLog(String oldSha, String newSha) throws IOException, GitAPIException {
		return logPlaceholder.getUnsaved(oldSha, newSha);
	}
	
	public interface LogPlaceholder {
		List<CommitBean> getInDatabase(Integer maxCount, Integer skip) throws IOException, GitAPIException;
		List<CommitBean> getInDatabase(String oldSha, String newSha) throws IOException, GitAPIException;
		List<GitCommit> getUnsaved(Integer maxCount, Integer skip) throws IOException, GitAPIException;
		List<GitCommit> getUnsaved(String oldSha, String newSha) throws IOException, GitAPIException;
	}
}
