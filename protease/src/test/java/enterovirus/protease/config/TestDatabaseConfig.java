package enterovirus.protease.config;

import javax.sql.DataSource;

import org.dbunit.ext.postgresql.PostgresqlDataTypeFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import com.github.springtestdbunit.bean.DatabaseConfigBean;
import com.github.springtestdbunit.bean.DatabaseDataSourceConnectionFactoryBean;

@Configuration
public class TestDatabaseConfig {
	
	@Profile("user_auth")
	@Bean
	public DataSource userAuthDataSource() {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName("org.postgresql.Driver");
		dataSource.setUrl("jdbc:postgresql://localhost:5432/user_auth");
		dataSource.setUsername("user_auth");
		dataSource.setPassword("postgres");
		return dataSource;
	}
	
	@Profile("one_repo_fix_commit")
	@Bean
	public DataSource oneRepoFixCommitDataSource() {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName("org.postgresql.Driver");
		dataSource.setUrl("jdbc:postgresql://localhost:5432/one_repo_fix_commit");
		dataSource.setUsername("one_repo_fix_commit");
		dataSource.setPassword("postgres");
		return dataSource;
	}
	
	@Profile("long_commit_path")
	@Bean
	public DataSource longCommitPathDataSource() {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName("org.postgresql.Driver");
		dataSource.setUrl("jdbc:postgresql://localhost:5432/long_commit_path");
		dataSource.setUsername("long_commit_path");
		dataSource.setPassword("postgres");
		return dataSource;
	}
	
	@Profile("production")
	@Bean
	public DataSource dataSource() {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName("org.postgresql.Driver");
		dataSource.setUrl("jdbc:postgresql://localhost:5432/enterovirus");
		dataSource.setUsername("enterovirus");
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
	public DatabaseDataSourceConnectionFactoryBean schemaSettingsDatabaseConnection() {
		DatabaseDataSourceConnectionFactoryBean dataConnection = new DatabaseDataSourceConnectionFactoryBean();
		dataConnection.setDataSource(dataSource());
		dataConnection.setDatabaseConfig(dbUnitDatabaseConfig());
		dataConnection.setSchema("settings");
		return dataConnection;
	}
//	
//	@Profile("fake_update")
//	@Bean
//	public DataSource fakeUpdateDataSource() {
//		DriverManagerDataSource dataSource = new DriverManagerDataSource();
//		dataSource.setDriverClassName("org.postgresql.Driver");
//		dataSource.setUrl("jdbc:postgresql://localhost:5432/hook_fake_update_dbname");
//		dataSource.setUsername("hook_fake_update_username");
//		dataSource.setPassword("hook_fake_update_password");
//		return dataSource;
//	}
}