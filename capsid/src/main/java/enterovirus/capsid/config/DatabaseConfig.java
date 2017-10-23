package enterovirus.capsid.config;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

@Configuration
public class DatabaseConfig {

	/* 
	 * In spring-boot, it should be possible to define the DataSource in
	 * /resources/application.properties
	 * 
	 * spring.datasource.url=jdbc:postgresql://localhost:5432/enterovirus
	 * spring.datasource.username=enterovirus_capsid
	 * spring.datasource.password=zooo
	 * spring.datasource.driver-class-name=org.postgresql.Driver
	 * 
	 * Since @ComponentScan is included in @SpringBootApplication,
	 * I can refer to that base class while testing.
	 * @ContextConfiguration(classes=CellReviewApplication.class)
	 * 
	 * However, when doing that, it will raise exceptions that it cannot
	 * @Autowired DataSource and JdbcTemplate. I don't understand why.
	 * The problem is solved after I marked the corresponding part of
	 * /resources/application.properties
	 * and write the setup in here.
	 */
	@Bean
	public DataSource dataSource() {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName("org.postgresql.Driver");
		dataSource.setUrl("jdbc:postgresql://localhost:5432/enterovirus");
		dataSource.setUsername("enterovirus_capsid");
		dataSource.setPassword("zooo");
		return dataSource;
	}
}