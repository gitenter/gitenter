package enterovirus.gitar;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.Random;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.junit.Test;

public class GitWorkspaceTest {

	static void addACommit(GitWorkspace workspace) throws IOException, GitAPIException {
		
		Random rand = new Random();
		String name = "file-"+String.valueOf(rand.nextInt(Integer.MAX_VALUE));
		
		new File(workspace, name).createNewFile();
		workspace.add(name);
		workspace.commit("commit message");
	}
	
	@Test
	public void test() {
		;
	}

}
