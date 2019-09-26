package com.gitenter.capsid.config;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

/* It was suggested to in https://vkuzel.com/spring-boot-jpa-hibernate-atomikos-postgresql-exception
 * to disable feature detection by this undocumented parameter
 * > spring.jpa.properties.hibernate.temp.use_jdbc_metadata_defaults = false
 * to suppress this error:
 * > java.sql.SQLFeatureNotSupportedException: Method org.postgresql.jdbc.PgConnection.createClob() is not yet implemented.
 * It doesn't work for our case because `com.gitenter.protease.config.JpaConfig` is in path.
 * Check the org.hibernate.engine.jdbc.internal.JdbcServiceImpl.configure method for more details.
 * And because detection is disabled we have to set correct dialect by hand.
 */

/*
 * To setup Postgres Dialect we are suggested to use the following setups in
 * Spring application.properties . However, we don't need to set it up because
 * we have `com.gitenter.protease.config.JpaConfig` in path. 
 * > spring.jpa.database-platform=org.hibernate.dialect.PostgreSQL95Dialect
 */
@Configuration
public class PostgresConfig {

	private Map<String, String> postgresUrlMap = new HashMap<String, String>() {
		private static final long serialVersionUID = 1L;
		{
			put("local", "localhost");
			put("docker", "postgres");
			put("staging", "staging-postgres.cqx7dy9nh94t.us-east-1.rds.amazonaws.com");
			put("production", "localhost");
		}
	};

	private DataSource getDataSource(String postgresUrl) {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName("org.postgresql.Driver");
		dataSource.setUrl("jdbc:postgresql://"+postgresUrl+":5432/gitenter");
		dataSource.setUsername("gitenter_app");
		dataSource.setPassword("zooo");
		return dataSource;
	}

	@Profile("local")
	@Bean
	public DataSource localDataSource() {
		return getDataSource(postgresUrlMap.get("local"));
	}

	@Profile("docker")
	@Bean
	public DataSource dockerDataSource() {
		return getDataSource(postgresUrlMap.get("docker"));
	}

	@Profile("staging")
	@Bean
	public DataSource stagingDataSource() {
		return getDataSource(postgresUrlMap.get("staging"));
	}

	@Profile("production")
	@Bean
	public DataSource productionDataSource() {
		return getDataSource(postgresUrlMap.get("production"));
	}
}
