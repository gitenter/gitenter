package enterovirus.protease.config;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

/*
 * The name of this class is "ProteaseDatabaseConfig" rather than
 * just "DatabaseConfig", is because more than one @Configuration
 * classes with the same name will cause naming crash (even if the
 * internal beans are different). However, we have other packages
 * which depend on "protease" also have some kind of database it
 * needs to configure.
 * 
 * It should causes by bean name crashing, for which the bean name
 * is the camelCase of the class name. The other method is to specify
 * the name of the bean manually.
 * 
 * The warning message is something like below:
 * 
 * WARNING: Exception encountered during context initialization - 
 * cancelling refresh attempt: 
 * org.springframework.beans.factory.BeanDefinitionStoreException: 
 * Failed to parse configuration class
 * [enterovirus.gihook.update.UpdateConfig]; nested exception is 
 * org.springframework.context.annotation.ConflictingBeanDefinitionException: 
 * Annotation-specified bean name 'databaseConfig' for bean class 
 * [enterovirus.gihook.update.testcase.long_commit_path.DatabaseConfig] 
 * conflicts with existing, non-compatible bean definition of same 
 * name and class [enterovirus.protease.config.DatabaseConfig]
 * 
 * TODO:
 * Naming crash happens even if this classes is in "src/test/java"
 * (so completely not in the final .jar package). It may be a bug of
 * STS. Need to be clarified later.
 */
@Configuration
public class ProteaseDatabaseConfig {
	
	@Profile("user_auth")
	@Bean
	public DataSource userAuthDataSource() {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName("org.postgresql.Driver");
		dataSource.setUrl("jdbc:postgresql://localhost:5432/user_auth_dbname");
		dataSource.setUsername("user_auth_username");
		dataSource.setPassword("user_auth_password");
		return dataSource;
	}
	
	@Profile("one_repo_fix_commit")
	@Bean
	public DataSource oneRepoFixCommitDataSource() {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName("org.postgresql.Driver");
		dataSource.setUrl("jdbc:postgresql://localhost:5432/one_repo_fix_commit_dbname");
		dataSource.setUsername("one_repo_fix_commit_username");
		dataSource.setPassword("one_repo_fix_commit_password");
		return dataSource;
	}
	
//	@Profile("long_commit_path")
//	@Bean
//	public DataSource longCommitPathDataSource() {
//		DriverManagerDataSource dataSource = new DriverManagerDataSource();
//		dataSource.setDriverClassName("org.postgresql.Driver");
//		dataSource.setUrl("jdbc:postgresql://localhost:5432/long_commit_path_dbname");
//		dataSource.setUsername("long_commit_path_username");
//		dataSource.setPassword("long_commit_path_password");
//		return dataSource;
//	}
//	
//	@Profile("fake_update")
//	@Bean
//	public DataSource fakeUpdateDataSource() {
//		DriverManagerDataSource dataSource = new DriverManagerDataSource();
//		dataSource.setDriverClassName("org.postgresql.Driver");
//		dataSource.setUrl("jdbc:postgresql://localhost:5432/hook_fake_update_dbname");
//		dataSource.setUsername("hook_fake_update_username");
//		dataSource.setPassword("hook_fake_update_password");
//		return dataSource;
//	}
}