package enterovirus.capsid.database;

import org.junit.runner.RunWith;
import org.hibernate.Hibernate;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import enterovirus.capsid.domain.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class OrganizationRepositoryTest {

	@Autowired
	private OrganizationRepository repository;
	
	@Test
	@Transactional
	public void findByUsername() throws Exception {
		
		OrganizationBean organization = repository.findByName("user1").get(0);
		
		System.out.println("===Organization Test Results===");
		System.out.println("Display Name: "+organization.getDisplayName());
		
		System.out.println("Managers: ");
		for (MemberInfoBean manager : organization.getManagers()) {
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
