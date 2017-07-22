package enterovirus.capsid.database;

import org.junit.runner.RunWith;
import org.hibernate.Hibernate;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import enterovirus.capsid.domain.*;

/*
 * @DataJpaTest cannot @Autowired the DataSource, so I use 
 * annotation @SpringBootTest
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class MemberRepositoryTest {

	@Autowired
	private MemberRepository repository;
	
	@Test
	@Transactional
	public void findByUsername() throws Exception {
		MemberBean member = repository.findByUsername("bell").get(0);
		System.out.println(member.getPassword());
		System.out.println(member.getDisplayName());
		
		//Hibernate.initialize(member.getOrganizations());
		for (OrganizationBean organization : member.getOrganizations()) {
			System.out.println(organization.getName());
		}
	}
}
