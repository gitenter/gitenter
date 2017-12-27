package enterovirus.gihook.update.testcase.client_side_fake;

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
		dataSource.setUrl("jdbc:postgresql://localhost:5432/hook_update_client_side_fake_dbname");
		dataSource.setUsername("hook_update_client_side_fake_username");
		dataSource.setPassword("hook_update_client_side_fake_password");
		return dataSource;
	}
}