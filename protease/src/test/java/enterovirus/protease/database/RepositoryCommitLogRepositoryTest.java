package enterovirus.protease.database;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import enterovirus.gitar.wrap.*;
import enterovirus.protease.*;
import enterovirus.protease.domain.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles(profiles = "long_commit_path")
@ContextConfiguration(classes=ProteaseConfig.class)
public class RepositoryCommitLogRepositoryTest {

	@Autowired RepositoryRepository repositoryRepository;
	@Autowired RepositoryCommitLogRepository repositoryCommitLogRepository;
	
	@Test
	public void test() throws Exception {
		RepositoryBean repository = repositoryRepository.findByOrganizationNameAndRepositoryName("org", "repo");
		BranchName branchName = new BranchName("master");
		repositoryCommitLogRepository.loadCommitLog(repository, branchName);
		for (CommitInfo info : repository.getCommitInfos()) {
			System.out.println(info.getCommitSha());
			System.out.println(info.getFullMessage());
		}
	}

}
