package enterovirus.gitar;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.io.IOException;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.RefNotFoundException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class GitBranchTest {
	
	@Rule public TemporaryFolder folder = new TemporaryFolder();

	@Test
	public void testBranchNotExist() throws IOException, GitAPIException {
		
		GitNormalRepository repository = GitNormalRepositoryTest.getOneEmpty(folder);
		assertEquals(repository.getBranch("branch-not-exist"), null);
	}
	
	/*
	 * TODO:
	 * 
	 * Currently returns `RefNotFoundException` with no HEAD error.
	 * However in git the actual return is:
	 * > fatal: Not a valid object name: 'master'.
	 * 
	 * Should correct the difference later, and/or define customized
	 * classes in here.
	 */
	@Test(expected = RefNotFoundException.class)
	public void testCreateBranchEmptyNormalRepository() throws IOException, GitAPIException {
		GitNormalRepository repository = GitNormalRepositoryTest.getOneEmpty(folder);
		repository.createBranch("a-branch");
	}
	
	@Test
	public void testCheckoutToFirstCommit() throws IOException, GitAPIException {
		
		GitNormalRepository repository = GitNormalRepositoryTest.getOneEmpty(folder);
		
		GitNormalBranch currentBranch = repository.getCurrentBranch();
		assertEquals(currentBranch.getName(), "master");
		assertEquals(repository.getBranches().size(), 0);
		assertEquals(repository.getBranch("master"), null);
		
		GitWorkspace workspace = currentBranch.checkoutTo();
		GitWorkspaceTest.addACommit(workspace);
		
		assertEquals(currentBranch.getName(), "master");
		assertEquals(repository.getBranches().size(), 1);
		assertNotEquals(repository.getBranch("master"), null);
	}

	@Test
	public void testSwitchBetweenMultipleBranchesNormalRepository() throws IOException, GitAPIException {
		
		GitNormalRepository repository = GitNormalRepositoryTest.getOneWithCommit(folder);
		
		repository.createBranch("a-branch");
		repository.createBranch("another-branch");
		
		assertEquals(repository.getBranches().size(), 3);
		assertEquals(repository.getCurrentBranch().getName(), "master");
		assertNotEquals(repository.getBranch("a-branch"), null);
		assertNotEquals(repository.getBranch("another-branch"), null);
		
		repository.getBranch("a-branch").checkoutTo();
		assertEquals(repository.getCurrentBranch().getName(), "a-branch");
		
		repository.getBranch("another-branch").checkoutTo();
		assertEquals(repository.getCurrentBranch().getName(), "another-branch");
	}
	
	@Test
	public void testCreateBranchBareRepository() throws IOException, GitAPIException {
		
		GitBareRepository repository = GitBareRepositoryTest.getOneWithCommit(folder);
		
		assertEquals(repository.getBranches().size(), 1);
		assertNotEquals(repository.getBranch("master"), null);
		
		repository.createBranch("a-branch");
		repository.createBranch("another-branch");
		
		assertEquals(repository.getBranches().size(), 3);
		assertNotEquals(repository.getBranch("a-branch"), null);
		assertNotEquals(repository.getBranch("another-branch"), null);
		
	}
}
