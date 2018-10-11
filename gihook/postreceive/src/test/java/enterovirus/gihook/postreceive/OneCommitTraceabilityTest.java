package enterovirus.gihook.postreceive;

import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AbstractTestExecutionListener;

import enterovirus.gitar.wrap.BranchName;
import enterovirus.gitar.wrap.CommitSha;
import enterovirus.protease.ProteaseConfig;
import enterovirus.protease.database.*;
import enterovirus.protease.domain.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles(profiles = "one_commit_traceability")
@ContextConfiguration(classes={ProteaseConfig.class,PostReceiveConfig.class})
public class OneCommitTraceabilityTest extends AbstractTestExecutionListener {
	
	@Autowired private UpdateDatabaseFromGit updateDatabase;
	
	@Autowired private RepositoryRepository repositoryRepository;
	@Autowired private CommitRepository commitRepository;
	@Autowired private DocumentRepository documentRepository;
	
	@Before
	public void buildDatabase() throws Exception {
		
		/*
		 * Need to first setup the environment (through shell) by:
		 * sh /home/beta/Workspace/enterovirus/test/one-commit-traceability/one-commit-traceability.sh
		 */
		
		File repositoryDirectory = new File("/home/beta/Workspace/enterovirus-test/one_commit_traceability/org/repo.git");
		File commitRecordFileMaster = new File("/home/beta/Workspace/enterovirus-test/one_commit_traceability/commit-sha-list.txt");
		
		HookInputSet status = new HookInputSet(
				repositoryDirectory,
				new BranchName("master"),
				new CommitSha("0000000000000000000000000000000000000000"),
				new CommitSha(commitRecordFileMaster, 1));
		
		updateDatabase.update(status);
	}
	
	@Test
	public void test() throws Exception {
		
		DocumentBean document = documentRepository.findByCommitIdAndRelativeFilepath(1, "requirement/R1.md");
	}
}
