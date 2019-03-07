package com.gitenter.post_receive_hook.config;

import java.io.File;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.gitenter.protease.source.GitSource;

@Configuration
public class GitConfig {

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
	public GitSource stsGitSource() {

		GitSource gitSource = new GitSource();
		gitSource.setRootFolderPath(new File(System.getProperty("user.home"), "Workspace/gitenter-test/local-git-server"));
		return gitSource;
	}

	@Profile("production")
	@Bean
	public GitSource productionGitSource() {

		GitSource gitSource = new GitSource();
		gitSource.setRootFolderPath("/home/git");
		return gitSource;
	}
}
