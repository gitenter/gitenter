package com.gitenter.post_receive_hook.config;

import java.io.File;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.gitenter.protease.config.source.GitSourceBean;

@Configuration
public class GitSourceConfig {

	/*
	 * Can't set it up as dummy, because although we only write to the database,
	 * we access git through the domain layer (rather than where this application
	 * is) which needs this setup.
	 *
	 * TODO:
	 * This is duplicated to the one in web application (capsid). Also setup
	 * need to be done in both places. Error prone.
	 */
	@Profile("sts")
	@Bean
	public GitSourceBean stsGitSourceBean() {
		
		GitSourceBean GitSourceBean = new GitSourceBean();
		GitSourceBean.setRootFolderPath(new File(System.getProperty("user.home"), "Workspace/gitenter-test/local-git-server"));
		return GitSourceBean;
	}
	
	@Profile("docker")
	@Bean
	public GitSourceBean dockerGitSourceBean() {
		
		GitSourceBean GitSourceBean = new GitSourceBean();
		GitSourceBean.setRootFolderPath("/home/git");
		return GitSourceBean;
	}

	@Profile("staging")
	@Bean
	public GitSourceBean stagingGitSourceBean() {
		
		GitSourceBean GitSourceBean = new GitSourceBean();
		GitSourceBean.setRootFolderPath("/data");
		return GitSourceBean;
	}
	
	@Profile("production")
	@Bean
	public GitSourceBean productionGitSourceBean() {
		
		GitSourceBean GitSourceBean = new GitSourceBean();
		GitSourceBean.setRootFolderPath("/data");
		return GitSourceBean;
	}
}
