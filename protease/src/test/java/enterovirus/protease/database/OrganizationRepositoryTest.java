package enterovirus.protease.database;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import enterovirus.protease.ApplicationConfig;
import enterovirus.protease.config.*;
import enterovirus.protease.domain.*;

/*
 * TODO:
 * Currently this test will run with error. The main reason is
 * JPA/Hibernate will not be compatible with SQL VIEWs. 
 * Need to find a way to map class inheritance to SQL using the
 * standard Hibernate way, rather than define a view by myself.
 * 
 * Error message:
 * WARN: GenerationTarget encountered exception accepting command : Error executing DDL via JDBC Statement
 * ...
 * Caused by: org.postgresql.util.PSQLException: ERROR: "current_document" is not a table or foreign table
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={ApplicationConfig.class})
public class OrganizationRepositoryTest {

	@Autowired private OrganizationRepository repository;
	
	@Test
	@Transactional
	public void findByUsername() throws Exception {
		
		OrganizationBean organization = repository.findByName("org1").get(0);
		
		System.out.println("===Organization Test Results===");
		System.out.println("Display Name: "+organization.getDisplayName());
		
		System.out.println("Managers: ");
		for (MemberBean manager : organization.getManagers()) {
			System.out.println("  --Username: "+manager.getUsername());
			System.out.println("    Display Name: "+manager.getDisplayName());
		}

		System.out.println("Repositories: ");
		for (RepositoryBean repository : organization.getRepositories()) {
			System.out.println("  --Name: "+repository.getName());
			System.out.println("    Display Name: "+repository.getDisplayName());
			System.out.println("    Git URI: "+repository.getGitUri());
		}
		
	}
}
