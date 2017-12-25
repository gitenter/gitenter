package enterovirus.proteinsistence.database;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import enterovirus.proteinsistence.ComponentScanConfig;
import enterovirus.proteinsistence.config.*;
import enterovirus.proteinsistence.domain.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={ComponentScanConfig.class,DatabaseConfig.class})
public class _MemberInfoRepositoryTest {

	@Autowired
	private _MemberInfoRepository repository;
	
	@Test
	@Transactional
	public void findByUsername() throws Exception {
		_MemberInfoBean member = repository.findByUsername("bell").get(0);
		System.out.println(member.getPassword());
		System.out.println(member.getDisplayName());
		
		//Hibernate.initialize(member.getOrganizations());
		for (OrganizationBean organization : member.getOrganizations()) {
			System.out.println(organization.getName());
		}
	}
}
