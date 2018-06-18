package com.gitenter.protease.config;

import java.io.File;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.gitenter.protease.source.GitSource;

@Configuration
public class TestGitConfig {
	
	@Profile("user_auth")
	@Bean
	public GitSource userAuthGitSource() {
		
		/* 
		 * Since this test doesn't touch the git part, non-existence doesn't matter. 
		 */ 
		GitSource gitSource = new GitSource();
		gitSource.setRootFolderPath("/dummy/position/not/exist"); 
		return gitSource;
	}

	@Profile("one_repo_fix_commit")
	@Bean
	public GitSource oneRepoFixCommitGitSource() {
		
		GitSource gitSource = new GitSource();
		gitSource.setRootFolderPath(new File(System.getProperty("user.home"), "Workspace/enterovirus-test/one_repo_fix_commit"));
		return gitSource;
	}
	
	@Profile("long_commit_path")
	@Bean
	public GitSource longCommitPathgitSource() {
		
		GitSource gitSource = new GitSource();
		gitSource.setRootFolderPath(new File(System.getProperty("user.home"), "Workspace/enterovirus-test/long_commit_path/"));
		return gitSource;
	}
	
	@Profile("production")
	@Bean
	public GitSource gitSource() {
		
		GitSource gitSource = new GitSource();
		gitSource.setRootFolderPath(new File(System.getProperty("user.home"), "Workspace/enterovirus-test/long_commit_path/"));
		return gitSource;
	}
}