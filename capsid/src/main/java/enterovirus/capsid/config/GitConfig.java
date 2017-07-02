package enterovirus.capsid.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import enterovirus.gitar.GitSource;

@Configuration
public class GitConfig {

	@Bean
	public GitSource gitSource() {
		GitSource gitSource = new GitSource();
		gitSource.setRootFolderPath("/home/beta/workspace/enterovirus_data");
		return gitSource;
	}
}
