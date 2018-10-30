package com.gitenter.hook.postreceive.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.gitenter.protease.source.GitSource;

@Configuration
public class GitConfig {

	@Profile("production")
	@Bean
	public GitSource gitSource() {
		
		/*
		 * GitSource is irrelevant, because this application never need to
		 * "read" the domain models under `git` schema (which uses this path
		 * to load the non-SQL part). It only (1) read the domain model under
		 * `auth` schema, and (2) "write" into the `git` schema, and both 
		 * doesn't need to setup the `GitSource`.
		 */
		GitSource gitSource = new GitSource();
		gitSource.setRootFolderPath("/not/relevant/fake/path");
		return gitSource;
	}
}
