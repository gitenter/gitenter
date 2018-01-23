package enterovirus.gihook.postreceive.config;

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

		/*
		 * This setup is used for UI automation tests:
		 * $ sh reset-dummy-data-using-ui.sh
		 */
		gitSource.setRootFolderPath("/home/beta/Workspace/enterovirus-dummy/server");
		
		return gitSource;
	}
}
