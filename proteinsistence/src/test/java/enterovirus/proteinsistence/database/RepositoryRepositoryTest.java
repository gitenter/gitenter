package enterovirus.proteinsistence.database;

import java.io.IOException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import enterovirus.proteinsistence.domain.RepositoryBean;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RepositoryRepositoryTest {

	@Autowired RepositoryRepository repositoryRepository;
	
	@Test
	public void test() throws IOException {
		RepositoryBean repository = repositoryRepository.findByOrganizationNameAndRepositoryName("org1", "repo1");
		System.out.println(repository.getDisplayName());
	}

}
