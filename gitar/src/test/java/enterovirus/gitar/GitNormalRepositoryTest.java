package enterovirus.gitar;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.Random;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.JGitInternalException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class GitNormalRepositoryTest {
	
	@Rule public TemporaryFolder folder = new TemporaryFolder();
	
	static GitNormalRepository getOneJustInitialized(TemporaryFolder folder) throws IOException, GitAPIException {
		
		Random rand = new Random();
		String name = "repo-"+String.valueOf(rand.nextInt(Integer.MAX_VALUE));
		
		File directory = folder.newFolder(name);
		return GitNormalRepository.getInstance(directory);
	}
	
	static GitNormalRepository getOneWithCommit(TemporaryFolder folder) throws IOException, GitAPIException {
		
		GitNormalRepository repository = getOneJustInitialized(folder);
		
		GitNormalBranch master = repository.getCurrentBranch();
		GitWorkspace workspace = master.checkoutTo();
		GitWorkspaceTest.addACommit(workspace, "First commit message");
		
		return repository;
	}

	@Test
	public void testInit() throws IOException, GitAPIException {
		
		File directory = folder.newFolder("repo");
		GitNormalRepository.getInstance(directory);
		
		assertTrue(new File(directory, ".git").isDirectory());
	}
	
	@Test(expected = JGitInternalException.class)
	public void testInitFolderNotExist() throws IOException, GitAPIException {
		
		File directory = new File("/a/path/which/does/not/exist");
		GitNormalRepository.getInstance(directory);
	}
	
	@Test(expected = JGitInternalException.class)
	public void testInitFolderReadOnly() throws IOException, GitAPIException {
		
		File directory = folder.newFolder("repo");
		directory.setReadOnly();
		
		GitNormalRepository.getInstance(directory);
	}
	
	@Test(expected = IOException.class)
	public void testDirectoryInitToOtherType() throws IOException, GitAPIException {
		
		File directory = folder.newFolder("repo.git");
		
		GitBareRepository.getInstance(directory);
		GitNormalRepository.getInstance(directory);
	}
	
	@Test
	public void testCreateAndUpdateAndGetRemote() throws IOException, GitAPIException {
	
		GitNormalRepository repository = getOneJustInitialized(folder);
		
		repository.createOrUpdateRemote("origin", "/fake/url");
		GitRemote origin = repository.getRemote("origin");
		assertEquals(origin.name, "origin");
		assertEquals(origin.url, "/fake/url");
		
		repository.createOrUpdateRemote("origin", "/another/fake/url");
		origin = repository.getRemote("origin");
		assertEquals(origin.url, "/another/fake/url");
	}
}
