package enterovirus.protease.database;

import java.io.IOException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import enterovirus.protease.*;
import enterovirus.protease.domain.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles(profiles = "user_auth")
@ContextConfiguration(classes=ProteaseConfig.class)
public class RepositoryRepositoryTest {

	@Autowired RepositoryRepository repositoryRepository;
	
	@Test
	@Transactional
	public void test() throws IOException {
		RepositoryBean repository = repositoryRepository.findByOrganizationNameAndRepositoryName("org1", "repo1");
		System.out.println(repository.getDisplayName());
		
		for (RepositoryMemberMapBean map : repository.getRepositoryMemberMaps()) {
			System.out.println(map.getMember().getUsername());
			System.out.println(map.getRole());
		}
	}
}
