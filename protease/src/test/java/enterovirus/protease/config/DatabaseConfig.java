package enterovirus.protease.config;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

@Configuration
public class DatabaseConfig {
	
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
}