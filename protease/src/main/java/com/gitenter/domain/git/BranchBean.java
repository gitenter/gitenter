package com.gitenter.domain.git;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.eclipse.jgit.api.errors.GitAPIException;

import com.gitenter.domain.auth.RepositoryBean;
import com.gitenter.gitar.util.GitPlaceholder;

import lombok.Getter;
import lombok.Setter;

public class BranchBean {
	
	@Getter
	final private String name;
	
	@Getter
	final private RepositoryBean repository;

	@Setter
	private HeadPlaceholder headPlaceholder;

	@Setter
	private LogPlaceholder logPlaceholder;
	
	public CommitBean getHead() throws IOException, GitAPIException {
		return headPlaceholder.get();
	}
	
	public List<CommitBean> getLog(Integer maxCount, Integer skip) throws IOException, GitAPIException {
		return logPlaceholder.get(maxCount, skip);
	}
	
	public BranchBean(String name, RepositoryBean repository) {
		this.repository = repository;
		this.name = name;
	}

	public interface HeadPlaceholder extends GitPlaceholder<CommitBean> {
		CommitBean get() throws IOException, GitAPIException;
	}
	
	public interface LogPlaceholder {
		List<CommitBean> get(Integer maxCount, Integer skip) throws IOException, GitAPIException;
	}
}
