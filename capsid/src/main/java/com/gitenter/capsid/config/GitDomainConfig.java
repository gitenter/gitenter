package com.gitenter.capsid.config;

import java.io.File;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.gitenter.capsid.config.bean.GitDomainSource;
import com.gitenter.protease.config.bean.GitSource;

@Configuration
public class GitDomainConfig {

	@Profile("local")
	@Bean
	public GitDomainSource stsdomainSource() {
		
		GitDomainSource gitDomainSource = new GitDomainSource();
		gitDomainSource.setGitSource(new GitSource(new File(System.getProperty("user.home"), "Workspace/gitenter-test/local-git-server")));
		gitDomainSource.setDomainName("localhost");
		gitDomainSource.setPort(22);
		return gitDomainSource;
	}
	
	@Profile("docker")
	@Bean
	public GitDomainSource localhostdomainSource() {
		
		/*
		 * Not using `GitSource` @Autowired by `GitSourceConfig`, because that's
		 * where the web-app container sees the git folder (not the git container sees).
		 */
		GitDomainSource gitDomainSource = new GitDomainSource();
		gitDomainSource.setGitSource(new GitSource("/home/git"));
		gitDomainSource.setDomainName("git");
		gitDomainSource.setPort(22);
		return gitDomainSource;
	}
	
	@Profile("staging")
	@Bean
	public GitDomainSource stagingdomainSource() {
		
		GitDomainSource gitDomainSource = new GitDomainSource();
		gitDomainSource.setGitSource(new GitSource("/home/git"));
		gitDomainSource.setDomainName("git.staging.gitenter.com");
		gitDomainSource.setPort(22);
		return gitDomainSource;
	}
	
	@Profile("production")
	@Bean
	public GitDomainSource productiondomainSource() {
		
		GitDomainSource gitDomainSource = new GitDomainSource();
		gitDomainSource.setGitSource(new GitSource("/home/git"));
		gitDomainSource.setDomainName("git.gitenter.com");
		gitDomainSource.setPort(22);
		return gitDomainSource;
	}
}
