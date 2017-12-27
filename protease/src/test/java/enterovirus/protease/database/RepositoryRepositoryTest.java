package enterovirus.protease.database;

import java.io.IOException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import enterovirus.protease.*;
import enterovirus.protease.domain.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes=UserAuthTestConfig.class)
public class RepositoryRepositoryTest {

	@Autowired RepositoryRepository repositoryRepository;
	
	@Test
	public void test() throws IOException {
		RepositoryBean repository = repositoryRepository.findByOrganizationNameAndRepositoryName("org1", "repo1");
		System.out.println(repository.getDisplayName());
	}

}
