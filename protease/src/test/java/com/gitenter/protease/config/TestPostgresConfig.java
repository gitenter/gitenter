package com.gitenter.protease.config;

import javax.sql.DataSource;

import org.dbunit.ext.postgresql.PostgresqlDataTypeFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import com.github.springtestdbunit.bean.DatabaseConfigBean;
import com.github.springtestdbunit.bean.DatabaseDataSourceConnectionFactoryBean;

@Configuration
public class TestPostgresConfig {
	
	@Bean
	public DataSource dataSource() {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName("org.postgresql.Driver");
		dataSource.setUrl("jdbc:postgresql://localhost:5432/gitenter");
		dataSource.setUsername("gitenter_app");
		dataSource.setPassword("zooo");
		return dataSource;
	}
	
	@Bean
	public DatabaseDataSourceConnectionFactoryBean schemaAuthDatabaseConnection() {
		return getDataConnectionFromSourceAndSchema(dataSource(), "auth");
	}
	
	@Bean
	public DatabaseDataSourceConnectionFactoryBean schemaGitDatabaseConnection() {
		return getDataConnectionFromSourceAndSchema(dataSource(), "git");
	}
	
	@Bean
	public DatabaseDataSourceConnectionFactoryBean schemaTraceabilityDatabaseConnection() {
		return getDataConnectionFromSourceAndSchema(dataSource(), "traceability");
	}
	
	@Bean
	public DatabaseDataSourceConnectionFactoryBean schemaReviewDatabaseConnection() {
		return getDataConnectionFromSourceAndSchema(dataSource(), "review");
	}
	
	@Bean
	public DatabaseConfigBean dbUnitDatabaseConfig() {
		DatabaseConfigBean config = new DatabaseConfigBean();
		config.setDatatypeFactory(new PostgresqlDataTypeFactory());
		config.setCaseSensitiveTableNames(true);
		return config;
	}
	
	private DatabaseDataSourceConnectionFactoryBean getDataConnectionFromSourceAndSchema(
			DataSource dataSource, String schemaName) {
		DatabaseDataSourceConnectionFactoryBean dataConnection = new DatabaseDataSourceConnectionFactoryBean();
		dataConnection.setDataSource(dataSource);
		dataConnection.setDatabaseConfig(dbUnitDatabaseConfig());
		dataConnection.setSchema(schemaName);
		return dataConnection;
		
	}
}