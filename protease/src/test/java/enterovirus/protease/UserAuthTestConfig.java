package enterovirus.protease;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = {
		"enterovirus.protease.config",
		"enterovirus.protease.database",
		"enterovirus.protease.domain",
		"enterovirus.protease.testcase.user_auth"})
public class UserAuthTestConfig {

}
