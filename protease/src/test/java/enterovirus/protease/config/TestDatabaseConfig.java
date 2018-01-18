package enterovirus.protease.config;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

@Configuration
public class TestDatabaseConfig {
	
	@Profile("user_auth")
	@Bean
	public DataSource userAuthDataSource() {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName("org.postgresql.Driver");
		dataSource.setUrl("jdbc:postgresql://localhost:5432/user_auth");
		dataSource.setUsername("user_auth");
		dataSource.setPassword("postgres");
		return dataSource;
	}
	
	@Profile("one_repo_fix_commit")
	@Bean
	public DataSource oneRepoFixCommitDataSource() {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName("org.postgresql.Driver");
		dataSource.setUrl("jdbc:postgresql://localhost:5432/one_repo_fix_commit");
		dataSource.setUsername("one_repo_fix_commit");
		dataSource.setPassword("postgres");
		return dataSource;
	}
	
	@Profile("long_commit_path")
	@Bean
	public DataSource longCommitPathDataSource() {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName("org.postgresql.Driver");
		dataSource.setUrl("jdbc:postgresql://localhost:5432/long_commit_path");
		dataSource.setUsername("long_commit_path");
		dataSource.setPassword("postgres");
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