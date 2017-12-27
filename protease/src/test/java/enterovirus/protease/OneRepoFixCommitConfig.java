package enterovirus.protease;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = {
		"enterovirus.protease.config",
		"enterovirus.protease.config.one_repo_fix_commit",
		"enterovirus.protease.database",
		"enterovirus.protease.domain"})
public class OneRepoFixCommitConfig {

}
