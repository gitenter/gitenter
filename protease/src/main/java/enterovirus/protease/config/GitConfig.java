package enterovirus.protease.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import enterovirus.gitar.GitSource;

@Configuration
public class GitConfig {

	@Bean
	public GitSource gitSource() {
		GitSource gitSource = new GitSource();

		/*
		 * This setup is used for local tests:
		 * $ sh reset-dummy-data-using-db.sh
		 */
		gitSource.setRootFolderPath("/home/beta/Workspace/enterovirus_data");

		/*
		 * This setup is used for UI automation tests:
		 * $ sh reset-dummy-data-using-ui.sh
		 */
//		gitSource.setRootFolderPath("/home/beta/Workspace/enterovirus-dummy/server");
		
		return gitSource;
	}
}
