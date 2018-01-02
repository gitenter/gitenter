package enterovirus.gihook.update.config;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

@Configuration
public class UpdateDatabaseConfig {
	
	@Profile("long_commit_path")
	@Bean
	public DataSource longCommitPathDataSource() {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName("org.postgresql.Driver");
		dataSource.setUrl("jdbc:postgresql://localhost:5432/long_commit_path_dbname");
		dataSource.setUsername("long_commit_path_username");
		dataSource.setPassword("long_commit_path_password");
		return dataSource;
	}
	
	@Profile("fake_update")
	@Bean
	public DataSource fakeUpdateDataSource() {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName("org.postgresql.Driver");
		dataSource.setUrl("jdbc:postgresql://localhost:5432/hook_fake_update_dbname");
		dataSource.setUsername("hook_fake_update_username");
		dataSource.setPassword("hook_fake_update_password");
		return dataSource;
	}
}
