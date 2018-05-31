package enterovirus.gitar;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.JGitInternalException;
import org.eclipse.jgit.lib.Repository;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class GitNormalRepositoryTest {
	
	@Rule public TemporaryFolder folder = new TemporaryFolder();

	@Test
	public void testInit() throws IOException, GitAPIException {
		
		File directory = folder.newFolder("repo");
		new GitNormalRepository(directory);
		
		assertTrue(new File(directory, ".git").isDirectory());
	}
	
	@Test(expected = JGitInternalException.class)
	public void testInitFolderNotExist() throws IOException, GitAPIException {
		
		File directory = new File("/a/path/which/does/not/exist");
		new GitNormalRepository(directory);
	}
	
	@Test(expected = JGitInternalException.class)
	public void testInitFolderReadOnly() throws IOException, GitAPIException {
		
		File directory = folder.newFolder("repo");
		directory.setReadOnly();
		
		new GitBareRepository(directory);
	}
	
	@Test(expected = IOException.class)
	public void testDirectoryInitToOtherType() throws IOException, GitAPIException {
		
		File directory = folder.newFolder("repo.git");
		
		new GitBareRepository(directory);
		new GitNormalRepository(directory);
	}
	
	@Test
	public void testRemote() throws IOException, GitAPIException {
	
		File directory = folder.newFolder("repo");
		GitNormalRepository repository = new GitNormalRepository(directory);
		
		repository.addRemote("origin", "/fake/url");
		assertEquals(repository.getRemote("origin").url, "/fake/url");
	}
	
	@Test
	public void testGetBranchNotExist() throws IOException, GitAPIException {
		
		File directory = folder.newFolder("repo");
		GitNormalRepository repository = new GitNormalRepository(directory);
		
//		repository.createBranch("branch-name");
//		assertTrue(repository.getBranch("branch-name").exist());
		
		assertFalse(repository.getBranch("branch-name-not-exist").exist());
	}
}
