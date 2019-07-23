package com.gitenter.post_receive_hook.config;

import static org.mockito.Mockito.mock;

import java.io.FileNotFoundException;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.gitenter.protease.config.bean.GitSource;

@Configuration
public class TestGitSourceConfig {
	
	@Bean
	public GitSource minimalGitSource() throws FileNotFoundException {
		
		GitSource gitSource = mock(GitSource.class);
		return gitSource;
	}
}
