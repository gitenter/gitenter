package com.gitenter.capsid.config;

import java.io.File;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.gitenter.protease.config.source.GitSourceBean;

@Configuration
public class GitSourceConfig {

	@Profile("sts")
	@Bean
	public GitSourceBean stsGitSource() {
		
		GitSourceBean gitSource = new GitSourceBean();
		gitSource.setRootFolderPath(new File(System.getProperty("user.home"), "Workspace/gitenter-test/local-git-server"));
		return gitSource;
	}
	
	@Profile("docker")
	@Bean
	public GitSourceBean dockerGitSource() {
		
		GitSourceBean gitSource = new GitSourceBean();
		gitSource.setRootFolderPath("/home/git");
		return gitSource;
	}

	@Profile("staging")
	@Bean
	public GitSourceBean stagingGitSource() {
		
		GitSourceBean gitSource = new GitSourceBean();
		gitSource.setRootFolderPath("/data");
		return gitSource;
	}
	
	@Profile("production")
	@Bean
	public GitSourceBean productionGitSource() {
		
		GitSourceBean gitSource = new GitSourceBean();
		gitSource.setRootFolderPath("/data");
		return gitSource;
	}
}
