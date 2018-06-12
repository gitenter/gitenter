package enterovirus.gitar;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class GitRootFolderTest {
	
	@Rule public TemporaryFolder folder = new TemporaryFolder();
	
	@Test
	public void testWithFiles() throws IOException, GitAPIException {
		
		GitNormalRepository repository = GitNormalRepositoryTest.getOneEmpty(folder);
		GitWorkspace workspace = repository.getCurrentBranch().checkoutTo();
		
		File file;
		
		file = folder.newFile("file-1");
		file.createNewFile();
		GitWorkspaceTest.add(workspace, file, "Add file-1");
		
		file = folder.newFile("file-2");
		file.createNewFile();
		GitWorkspaceTest.add(workspace, file, "Add file-2");
		
		GitCommit commit = repository.getCurrentBranch().getHead();
		GitRootFolder folder = commit.getRootFolder();
		
		assertEquals(folder.list().size(), 2);
		
		String[] filenames = new String[2];
		int i = 0;
		for (GitPath path : folder.list()) {
			filenames[i] = path.getName();
			++i;
		}
		String[] desiredFilenames = new String[]{"file-1", "file-2"};
		
		assertArrayEquals(filenames, desiredFilenames);
	}
	
	@Test
	public void testWithFolder() throws IOException, GitAPIException {	
		
		GitNormalRepository repository = GitNormalRepositoryTest.getOneEmpty(folder);
		GitWorkspace workspace = repository.getCurrentBranch().checkoutTo();
		
		File file;

		file = folder.newFolder("folder-1");
		new File(file, "file-1-in-folder-1").createNewFile();
		new File(file, "file-2-in-folder-1").createNewFile();
		GitWorkspaceTest.add(workspace, file, "Add folder-1");
		
		GitCommit commit = repository.getCurrentBranch().getHead();
		GitRootFolder folder = commit.getRootFolder();
		
		assertEquals(folder.list().size(), 1);
		
		GitFolder folder1 = folder.cd("folder-1");
		assertEquals(folder1.list().size(), 2);
		
		String[] filenames = new String[2];
		int i = 0;
		for (GitPath path : folder1.list()) {
			filenames[i] = path.getName();
			++i;
		}
		String[] desiredFilenames = new String[]{"file-1-in-folder-1", "file-2-in-folder-1"};
		
		assertArrayEquals(filenames, desiredFilenames);
	}
	
	@Test
	public void testEmpty() throws IOException, GitAPIException {
		
		GitNormalRepository repository = GitNormalRepositoryTest.getOneEmpty(folder);
		GitWorkspace workspace = repository.getCurrentBranch().checkoutTo();
		
		File file = folder.newFile("file");
		file.createNewFile();
		GitWorkspaceTest.add(workspace, file, "Add file");
		
		GitWorkspaceTest.deleteAll(workspace);
		
		GitCommit commit = repository.getCurrentBranch().getHead();
		GitRootFolder folder = commit.getRootFolder();
		
		assertEquals(folder.list().size(), 0);
		
		/*
		 * Even for commit with empty workspace, the commit SHA itself it not
		 * all-zero/empty. Empty only happens when 
		 */
		assertNotEquals(commit.getSha(), GitCommit.EMPTY_SHA);
	}
}
