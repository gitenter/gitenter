package enterovirus.protease.database;

import java.io.IOException;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import enterovirus.protease.*;
import enterovirus.protease.domain.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles(profiles = "user_auth")
@ContextConfiguration(classes=ProteaseConfig.class)
public class RepositoryMemberMapRepositoryTest {

	@Autowired RepositoryMemberMapRepository repository;
	
	@Test
	public void test() throws IOException {
		
		List<RepositoryMemberMapBean> maps = repository.findByUsernameAndOrganizationNameAndRepositoryName("user1", "org1", "repo1");

		for (RepositoryMemberMapBean map : maps) {
			System.out.println(map.getRole());
		}
		
//		repository.deleteById(2);
	}
}
