package com.gitenter.dao.auth;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.gitenter.domain.auth.RepositoryBean;
import com.gitenter.domain.git.BranchBean;
import com.gitenter.gitar.GitBareRepository;
import com.gitenter.gitar.GitBranch;
import com.gitenter.gitar.GitRepository;
import com.gitenter.protease.source.GitSource;

public class RealBranchList implements BranchList {

	/*
	 * Technically we cannot @Autowired "gitSource" in here
	 * (because Spring @Component doesn't accept a non-trivial
	 * constructor with >=1 arguments. So we get gitSource from
	 * repository/DAO and pass the information all the way done
	 * to here.
	 */
	private GitSource gitSource;
	private RepositoryBean repository;
	
	public RealBranchList(GitSource gitSource, RepositoryBean repository) {
		this.gitSource = gitSource;
		this.repository = repository;
	}
	
	@Override
	public Collection<BranchBean> getBranches() throws IOException, GitAPIException {
		
		File repositoryDirectory = gitSource.getBareRepositoryDirectory(
				repository.getOrganization().getName(), 
				repository.getName());
		
		GitRepository gitRepository = GitBareRepository.getInstance(repositoryDirectory);
		Collection<GitBranch> gitBranches = gitRepository.getBranches();
		
		Collection<BranchBean> branches = new ArrayList<BranchBean>();
		for (GitBranch gitBranch : gitBranches) {
			branches.add(new BranchBean(gitBranch.getName()));
		}
		
		return branches;
	}

}
