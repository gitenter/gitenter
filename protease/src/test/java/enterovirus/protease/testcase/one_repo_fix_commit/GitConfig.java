package enterovirus.protease.testcase.one_repo_fix_commit;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import enterovirus.gitar.GitSource;

@Configuration
public class GitConfig {

	@Bean
	public GitSource gitSource() {
		
		GitSource gitSource = new GitSource();
		
		gitSource.setRootFolderPath("/home/beta/Workspace/enterovirus-test/one-repo-fix-commit");
		
		return gitSource;
	}
}