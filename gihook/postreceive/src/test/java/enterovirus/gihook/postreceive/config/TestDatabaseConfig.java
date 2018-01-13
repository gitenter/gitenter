package enterovirus.gihook.postreceive.config;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

/*
 * Should have a different name with DatabaseConfig in the main code.
 * Otherwise it compiles error.
 */
@Configuration
public class TestDatabaseConfig {
	
	@Profile("long_commit_path")
	@Bean
	public DataSource longCommitPathDataSource() {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName("org.postgresql.Driver");
		dataSource.setUrl("jdbc:postgresql://localhost:5432/long_commit_path_dbname");
		dataSource.setUsername("long_commit_path_username");
		dataSource.setPassword("postgres");
		return dataSource;
	}
	
	@Profile("one_commit_traceability")
	@Bean
	public DataSource oneCommitTraceabilityDataSource() {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName("org.postgresql.Driver");
		dataSource.setUrl("jdbc:postgresql://localhost:5432/one_commit_traceability_dbname");
		dataSource.setUsername("one_commit_traceability_username");
		dataSource.setPassword("postgres");
		return dataSource;
	}
}
