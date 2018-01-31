package enterovirus.immunessh.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import enterovirus.protease.source.GitSource;

@Configuration
public class GitConfig {

	@Bean
	public GitSource gitSource() {
		
		GitSource gitSource = new GitSource();
		gitSource.setRootFolderPath("/not/relevant/fake/path");
		return gitSource;
	}
}
