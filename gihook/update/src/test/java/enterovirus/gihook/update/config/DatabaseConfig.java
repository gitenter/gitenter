package enterovirus.gihook.update.config;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

@Configuration
public class DatabaseConfig {

//	@Bean
//	public DataSource longCommitPathDataSource() {
//		DriverManagerDataSource dataSource = new DriverManagerDataSource();
//		dataSource.setDriverClassName("org.postgresql.Driver");
//		dataSource.setUrl("jdbc:postgresql://localhost:5432/long_commit_path_dbname");
//		dataSource.setUsername("long_commit_path_username");
//		dataSource.setPassword("long_commit_path_password");
//		return dataSource;
//	}
}
