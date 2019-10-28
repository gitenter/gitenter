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
		return new GitSource(new File(System.getProperty("user.home"), "Workspace/gitenter-test/local-git-server"));
	}
	
	/*
	 * This is the path web-app container sees, not the path for git container sees.
	 */
	@Profile("docker")
	@Bean
	public GitSource dockerGitSource() {
		return new GitSource("/data");
	}

	@Profile("staging")
	@Bean
	public GitSource stagingGitSource() {
		return new GitSource("/data");
	}
	
	@Profile("production")
	@Bean
	public GitSource productionGitSource() {
		return new GitSource("/data");
	}
}
