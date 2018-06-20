package com.gitenter.dao.auth;

import java.io.IOException;
import java.util.Collection;

import org.eclipse.jgit.api.errors.GitAPIException;

import com.gitenter.domain.auth.RepositoryBean;
import com.gitenter.domain.git.BranchBean;
import com.gitenter.protease.source.GitSource;

public class ProxyBranchList implements BranchList {

	private RealBranchList branchList = null;

	private GitSource gitSource;
	private RepositoryBean repository;
	
	public ProxyBranchList(GitSource gitSource, RepositoryBean repository) {
		this.gitSource = gitSource;
		this.repository = repository;
	}
	
	@Override
	public Collection<BranchBean> getBranches() throws IOException, GitAPIException {
		
		if (branchList == null) {
			branchList = new RealBranchList(gitSource, repository);
		}
		
		return branchList.getBranches();
	}

}
