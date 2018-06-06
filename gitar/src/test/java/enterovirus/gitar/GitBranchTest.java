package enterovirus.gitar;

import static org.junit.Assert.*;

import java.io.IOException;

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
		assertEquals(repository.getBranches().size(), 0);
		assertEquals(repository.getCurrentBranch().getName(), "master");
		
		GitWorkspace workspace = master.checkoutTo();
		GitWorkspaceTest.addACommit(workspace);
		
		assertTrue(master.exist());
		assertEquals(repository.getBranches().size(), 1);
		assertEquals(repository.getCurrentBranch().getName(), "master");
	}

	@Test
	public void testSwitchBetweenMultipleBranches() throws IOException, GitAPIException {
		
		GitNormalRepository repository = GitNormalRepositoryTest.getOne(folder);
		
		GitBranch master = repository.getBranch("master");
		GitWorkspace workspace = master.checkoutTo();
		GitWorkspaceTest.addACommit(workspace);
		
		repository.createBranch("a-branch");
		repository.createBranch("another-branch");
		
		assertEquals(repository.getBranches().size(), 3);
		assertEquals(repository.getCurrentBranch().getName(), "master");
		assertTrue(repository.getBranch("a-branch").exist());
		assertTrue(repository.getBranch("another-branch").exist());
		
		repository.getBranch("a-branch").checkoutTo();
		assertEquals(repository.getCurrentBranch().getName(), "a-branch");
		
		repository.getBranch("another-branch").checkoutTo();
		assertEquals(repository.getCurrentBranch().getName(), "another-branch");
	}
}
