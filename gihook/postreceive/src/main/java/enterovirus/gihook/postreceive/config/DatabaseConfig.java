package enterovirus.gihook.postreceive.config;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

@Configuration
public class DatabaseConfig {

	/*
	 * This bean need to have a profile, because otherwise in the
	 * test sets they'll @Autowired to this bean rather than the
	 * one with specific profile.
	 * 
	 * There seems no way to setup in spring with @Profile with higher
	 * priority, and the bean without @Profile to have low priority.
	 * Just need to set them up either all or none.
	 */
	@Profile("production")
	@Bean
	public DataSource dataSource() {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName("org.postgresql.Driver");
		dataSource.setUrl("jdbc:postgresql://localhost:5432/enterovirus");
//		dataSource.setUsername("enterovirus_capsid");
		dataSource.setUsername("enterovirus");
		dataSource.setPassword("zooo");
		return dataSource;
	}
}