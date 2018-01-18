package enterovirus.protease.database;

import java.util.Map;

import org.junit.Before;
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
public class RepositoryGitDAOTest {

	@Autowired private RepositoryRepository repositoryRepository;
	@Autowired private RepositoryGitDAO repositoryGitDAO;
	
	private RepositoryBean repository;
	
	@Before
	public void init() throws Exception {
		repository = repositoryRepository.findByOrganizationNameAndRepositoryName("org", "repo");
	}
	
	@Test
	public void test1() throws Exception {
		
		/*
		 * TODO:
		 * Currently, it can run but the number of results doesn't match.
		 */
		BranchName branchName = new BranchName("master");
		repositoryGitDAO.loadCommitLog(repository, branchName);
		for (Map.Entry<CommitInfo,CommitBean> entry : repository.getCommitLogMap().entrySet()) {
			
			CommitInfo commitInfo = entry.getKey();
			CommitBean commit = entry.getValue();
			
			System.out.println(commitInfo.getCommitSha());
			System.out.println(commitInfo.getFullMessage());
			System.out.println(commit.getClass());
		}
	}
	
	@Test
	public void test2() throws Exception {
		
		repositoryGitDAO.loadBranchNames(repository);
		for (BranchName name : repository.getBranchNames()) {
			System.out.println(name.getName());
		}
	}

}
