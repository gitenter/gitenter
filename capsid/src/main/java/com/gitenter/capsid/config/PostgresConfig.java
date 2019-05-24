package com.gitenter.capsid.config;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

@Configuration
public class PostgresConfig {

	@Profile("sts")
	@Bean
	public DataSource stsDataSource() {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName("org.postgresql.Driver");
		dataSource.setUrl("jdbc:postgresql://localhost:5432/gitenter");
		dataSource.setUsername("gitenter_app");
		dataSource.setPassword("zooo");
		return dataSource;
	}

	@Profile("docker")
	@Bean
	public DataSource dockerDataSource() {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName("org.postgresql.Driver");
		dataSource.setUrl("jdbc:postgresql://database:5432/gitenter");
		dataSource.setUsername("gitenter_app");
		dataSource.setPassword("zooo");
		return dataSource;
	}

	@Profile("qa")
	@Bean
	public DataSource qaDataSource() {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName("org.postgresql.Driver");
		dataSource.setUrl("jdbc:postgresql://ecs-circleci-qa-postgres.cqx7dy9nh94t.us-east-1.rds.amazonaws.com:5432/gitenter");
		dataSource.setUsername("gitenter_app");
		dataSource.setPassword("zooo");
		return dataSource;
	}

	@Profile("production")
	@Bean
	public DataSource productionDataSource() {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName("org.postgresql.Driver");
		dataSource.setUrl("jdbc:postgresql://localhost:5432/gitenter");
		dataSource.setUsername("gitenter_app");
		dataSource.setPassword("zooo");
		return dataSource;
	}
}
