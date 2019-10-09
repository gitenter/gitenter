package com.gitenter.post_receive_hook.config;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

@Configuration
public class PostgresConfig {

	private Map<String, String> postgresUrlMap = new HashMap<String, String>() {
		private static final long serialVersionUID = 1L;
		{
			put("local", "localhost");
			put("docker", "postgres");
			put("staging", "staging-ecs.cqx7dy9nh94t.us-east-1.rds.amazonaws.com");
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