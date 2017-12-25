package enterovirus.protease;

import java.util.Properties;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@ComponentScan
//@EnableJpaRepositories(basePackages="enterovirus.protease.database", entityManagerFactoryRef="emf")
@EnableJpaRepositories(basePackages="enterovirus.protease.database")
@EnableTransactionManagement
public class ComponentScanConfig {

	@Autowired
	DataSource dataSource;
	
	  @Bean
	  public HibernateJpaVendorAdapter jpaVendorAdapter() {
	    HibernateJpaVendorAdapter adapter = new HibernateJpaVendorAdapter();
	    adapter.setDatabase(Database.POSTGRESQL);
	    adapter.setShowSql(true);
	    adapter.setGenerateDdl(true);
	    adapter.setDatabasePlatform("org.hibernate.dialect.PostgreSQL94Dialect");
	    return adapter;
	  }
	
//	@Bean(name="emf")
	  @Bean
	  public EntityManagerFactory entityManagerFactory() {

//	    HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
//	    vendorAdapter.setGenerateDdl(true);
		
	    LocalContainerEntityManagerFactoryBean entityManagerFactory = new LocalContainerEntityManagerFactoryBean();
	    entityManagerFactory.setDataSource(dataSource);
	    entityManagerFactory.setPackagesToScan(new String[]{"enterovirus.protease.database", "enterovirus.protease.domain"});
	    entityManagerFactory.setJpaVendorAdapter(jpaVendorAdapter());
	    
	    Properties properties = new Properties();
	    properties.setProperty("hibernate.dialect", "org.hibernate.dialect.PostgreSQL94Dialect");
	    entityManagerFactory.setJpaProperties(properties);
	    
	    entityManagerFactory.afterPropertiesSet();
	    return entityManagerFactory.getObject();
	  }
	


	

	  @Bean
	  public PlatformTransactionManager transactionManager() {

	    JpaTransactionManager txManager = new JpaTransactionManager();
//	    txManager.setEntityManagerFactory(entityManagerFactory());
	    return txManager;
	  }
}
