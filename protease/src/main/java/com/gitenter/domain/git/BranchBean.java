package com.gitenter.domain.git;

import java.io.IOException;
import java.util.List;

import org.eclipse.jgit.api.errors.GitAPIException;

import com.gitenter.domain.auth.RepositoryBean;
import com.gitenter.gitar.util.GitPlaceholder;

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
	
	public List<CommitBean> getLog(Integer maxCount, Integer skip) throws IOException, GitAPIException {
		return logPlaceholder.get(maxCount, skip);
	}
	
	public interface LogPlaceholder {
		List<CommitBean> get(Integer maxCount, Integer skip) throws IOException, GitAPIException;
	}
}
