package com.gitenter.domain.git;

import java.io.IOException;
import java.util.Collection;

import org.eclipse.jgit.api.errors.GitAPIException;

import com.gitenter.domain.auth.RepositoryBean;
import com.gitenter.gitar.util.GitPlaceholder;

import lombok.Getter;
import lombok.Setter;

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
	
	@Setter
	private CommitPlaceholder commitPlaceholder;
	
	public TagBean(String name, RepositoryBean repository) {
		this.name = name;
		this.repository = repository;
	}
	
	public CommitBean getCommit() throws IOException, GitAPIException {
		return commitPlaceholder.get();
	}

	public interface CommitPlaceholder extends GitPlaceholder<CommitBean> {
		CommitBean get() throws IOException, GitAPIException;
	}
}
