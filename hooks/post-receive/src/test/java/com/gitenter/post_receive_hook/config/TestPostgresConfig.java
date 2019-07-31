package com.gitenter.post_receive_hook.config;

import static org.mockito.Mockito.mock;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

/*
 * Should have a different name with DatabaseConfig in the main code.
 * Otherwise it compiles error.
 */
@Configuration
public class TestPostgresConfig {
	
	@Bean
	public DataSource dataSource() {
		DriverManagerDataSource dataSource = mock(DriverManagerDataSource.class);
		return dataSource;
	}
}
