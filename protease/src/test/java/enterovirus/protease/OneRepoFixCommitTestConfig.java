package enterovirus.protease;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("one_repo_fix_commit")
@ComponentScan(basePackages = {
		"enterovirus.protease.config",
		"enterovirus.protease.database",
		"enterovirus.protease.domain"})
public class OneRepoFixCommitTestConfig {

}
