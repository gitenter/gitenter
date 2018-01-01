package enterovirus.gihook.update.testcase.long_commit_path;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import enterovirus.gitar.GitSource;

@Configuration
public class GitConfig {

	@Bean
	@Primary
	public GitSource gitSource() {
		
		GitSource gitSource = new GitSource();
		
		gitSource.setRootFolderPath("/home/beta/Workspace/enterovirus-test/hook-fake-update/org/repo.git");
		
		return gitSource;
	}
}
