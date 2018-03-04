package enterovirus.gihook.precommit.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import enterovirus.protease.source.GitSource;

@Configuration
public class GitConfig {

	@Profile("production")
	@Bean
	public GitSource gitSource() {
		
		GitSource gitSource = new GitSource();
		gitSource.setRootFolderPath("/not/relevant/fake/path");
		return gitSource;
	}
}
