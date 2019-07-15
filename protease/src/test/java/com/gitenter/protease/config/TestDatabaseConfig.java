package com.gitenter.protease.config;

import javax.sql.DataSource;

import org.dbunit.ext.postgresql.PostgresqlDataTypeFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import com.github.springtestdbunit.bean.DatabaseConfigBean;
import com.github.springtestdbunit.bean.DatabaseDataSourceConnectionFactoryBean;

@Configuration
public class TestDatabaseConfig {
	
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
	public DatabaseConfigBean dbUnitDatabaseConfig() {
		DatabaseConfigBean config = new DatabaseConfigBean();
		config.setDatatypeFactory(new PostgresqlDataTypeFactory());
		config.setCaseSensitiveTableNames(true);
		return config;
	}
	
	@Bean
	public DatabaseDataSourceConnectionFactoryBean schemaAuthDatabaseConnection() {
		DatabaseDataSourceConnectionFactoryBean dataConnection = new DatabaseDataSourceConnectionFactoryBean();
		dataConnection.setDataSource(dataSource());
		dataConnection.setDatabaseConfig(dbUnitDatabaseConfig());
		dataConnection.setSchema("auth");
		return dataConnection;
	}
	
	@Bean
	public DatabaseDataSourceConnectionFactoryBean schemaGitDatabaseConnection() {
		DatabaseDataSourceConnectionFactoryBean dataConnection = new DatabaseDataSourceConnectionFactoryBean();
		dataConnection.setDataSource(dataSource());
		dataConnection.setDatabaseConfig(dbUnitDatabaseConfig());
		dataConnection.setSchema("git");
		return dataConnection;
	}
	
	@Bean
	public DatabaseDataSourceConnectionFactoryBean schemaTraceabilityDatabaseConnection() {
		DatabaseDataSourceConnectionFactoryBean dataConnection = new DatabaseDataSourceConnectionFactoryBean();
		dataConnection.setDataSource(dataSource());
		dataConnection.setDatabaseConfig(dbUnitDatabaseConfig());
		dataConnection.setSchema("traceability");
		return dataConnection;
	}
	
	@Bean
	public DatabaseDataSourceConnectionFactoryBean schemaReviewDatabaseConnection() {
		DatabaseDataSourceConnectionFactoryBean dataConnection = new DatabaseDataSourceConnectionFactoryBean();
		dataConnection.setDataSource(dataSource());
		dataConnection.setDatabaseConfig(dbUnitDatabaseConfig());
		dataConnection.setSchema("review");
		return dataConnection;
	}
}