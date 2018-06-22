package com.gitenter.domain.git;

import java.io.IOException;
import java.util.List;

import org.eclipse.jgit.api.errors.GitAPIException;

import com.gitenter.domain.auth.RepositoryBean;

import lombok.Getter;

public class BranchBean {
	
	@Getter
	final private String name;
	
	@Getter
	final private RepositoryBean repository;

	private HeadPlaceholder headPlaceholder;

	private LogPlaceholder logPlaceholder;
	
	public CommitBean getHead() throws IOException, GitAPIException {
		return headPlaceholder.getHead();
	}
	
	public List<CommitBean> getLog(Integer maxCount, Integer skip) throws IOException, GitAPIException {
		return logPlaceholder.getLog(maxCount, skip);
	}
	
	public BranchBean(
			String name, 
			RepositoryBean repository, 
			HeadPlaceholder headPlaceholder,
			LogPlaceholder logPlaceholder) {
		this.repository = repository;
		this.name = name;
		this.headPlaceholder = headPlaceholder;
		this.logPlaceholder = logPlaceholder;
	}

	public interface HeadPlaceholder {
		CommitBean getHead() throws IOException, GitAPIException;
	}
	
	public interface LogPlaceholder {
		List<CommitBean> getLog(Integer maxCount, Integer skip) throws IOException, GitAPIException;
	}
}
