package enterovirus.gitar;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class GitBranchTest {
	
	@Rule public TemporaryFolder folder = new TemporaryFolder();

	@Test
	public void testBranchNotExist() throws IOException, GitAPIException {
		
		GitNormalRepository repository = GitNormalRepositoryTest.getOne(folder);
		
		assertFalse(repository.getBranch("branch-not-exist").exist());
	}
	
	@Test
	public void testCheckoutToFirstCommit() throws IOException, GitAPIException {
		
		GitNormalRepository repository = GitNormalRepositoryTest.getOne(folder);
		
		GitBranch master = repository.getBranch("master");
		assertFalse(master.exist());
		
		GitWorkspace workspace = master.checkoutTo();
		GitWorkspaceTest.addACommit(workspace);
		assertTrue(master.exist());
	}

	@Test
	public void testSwitchBetweenMultipleBranches() throws IOException, GitAPIException {
		
		GitNormalRepository repository = GitNormalRepositoryTest.getOne(folder);
		GitBranch master = repository.getBranch("master");
		GitWorkspace workspace = master.checkoutTo();
		GitWorkspaceTest.addACommit(workspace);
		
		repository.createBranch("a-branch");
		repository.createBranch("another-branch");
		
		assertTrue(repository.getBranch("a-branch").exist());
		assertTrue(repository.getBranch("another-branch").exist());
		
//		assertEquals("master", repository.getCurrentBranch().getName());
		
		repository.getBranch("a-branch").checkoutTo();
	}
}
