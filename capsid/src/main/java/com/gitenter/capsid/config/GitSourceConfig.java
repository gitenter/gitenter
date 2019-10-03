package com.gitenter.capsid.config;

import java.io.File;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.gitenter.protease.config.bean.GitSource;

@Configuration
public class GitSourceConfig {

	@Profile("local")
	@Bean
	public GitSource stsGitSource() {
		
		GitSource gitSource = new GitSource();
		gitSource.setRootFolderPath(new File(System.getProperty("user.home"), "Workspace/gitenter-test/local-git-server"));
		return gitSource;
	}
	
	@Profile("docker")
	@Bean
	public GitSource dockerGitSource() {
		
		GitSource gitSource = new GitSource();
		gitSource.setRootFolderPath("/data");
		return gitSource;
	}

	@Profile("staging")
	@Bean
	public GitSource stagingGitSource() {
		
		GitSource gitSource = new GitSource();
		gitSource.setRootFolderPath("/data");
		return gitSource;
	}
	
	@Profile("production")
	@Bean
	public GitSource productionGitSource() {
		
		GitSource gitSource = new GitSource();
		gitSource.setRootFolderPath("/data");
		return gitSource;
	}
}
