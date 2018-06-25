package com.gitenter.domain.git;

import java.io.IOException;

import org.eclipse.jgit.api.errors.GitAPIException;

import com.gitenter.domain.auth.RepositoryBean;

import lombok.Getter;

/*
 * TODO:
 * Implement the annotated/lightweight hierarchy in here.
 */
@Getter
public class TagBean {

	@Getter
	final private String name;
	
	@Getter
	final private RepositoryBean repository;
	
	private CommitPlaceholder commitPlaceholder;
	
	public TagBean(String name, RepositoryBean repository, CommitPlaceholder commitPlaceholder) {
		this.name = name;
		this.repository = repository;
		this.commitPlaceholder = commitPlaceholder;
	}
	
	public CommitBean getCommit() throws IOException, GitAPIException {
		return commitPlaceholder.get();
	}

	public interface CommitPlaceholder {
		CommitBean get() throws IOException, GitAPIException;
	}
}