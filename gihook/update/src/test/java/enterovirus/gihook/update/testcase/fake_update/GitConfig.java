package enterovirus.gihook.update.testcase.fake_update;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import enterovirus.gitar.GitSource;

@Configuration
public class GitConfig {

	@Bean
	public GitSource gitSource() {
		
		GitSource gitSource = new GitSource();
		
		gitSource.setRootFolderPath("/home/beta/Workspace/enterovirus-test/long-commit-path/org/repo.git");
		
		return gitSource;
	}
}
