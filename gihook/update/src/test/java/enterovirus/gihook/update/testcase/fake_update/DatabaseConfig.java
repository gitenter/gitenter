package enterovirus.gihook.update.testcase.fake_update;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

@Configuration
@Qualifier("fakeUpdate")
public class DatabaseConfig {

	@Bean
	public DataSource dataSource() {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName("org.postgresql.Driver");
		dataSource.setUrl("jdbc:postgresql://localhost:5432/hook_fake_update_dbname");
		dataSource.setUsername("hook_fake_update_username");
		dataSource.setPassword("hook_fake_update_password");
		return dataSource;
	}
}