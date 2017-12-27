package enterovirus.protease.testcase.one_repo_fix_commit;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

@Configuration
public class DatabaseConfig {

	/*
	 * TODO:
	 * Need to later define a SQL user for this persistence layer.
	 */
	@Bean
	public DataSource dataSource() {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName("org.postgresql.Driver");
		dataSource.setUrl("jdbc:postgresql://localhost:5432/one_repo_fix_commit_dbname");
		dataSource.setUsername("one_repo_fix_commit_username");
		dataSource.setPassword("one_repo_fix_commit_password");
		return dataSource;
	}
}