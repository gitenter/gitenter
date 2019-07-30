package com.gitenter.post_receive_hook.config;

import static org.mockito.Mockito.mock;

import java.io.FileNotFoundException;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.gitenter.protease.config.source.GitSourceBean;

@Configuration
public class TestGitSourceConfig {
	
	@Bean
	public GitSourceBean minimalGitSource() throws FileNotFoundException {
		
		GitSourceBean gitSource = mock(GitSourceBean.class);
		return gitSource;
	}
}
