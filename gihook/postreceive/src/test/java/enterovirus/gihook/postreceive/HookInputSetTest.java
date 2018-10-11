package enterovirus.gihook.postreceive;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;

public class HookInputSetTest {

	@Test
	public void test() throws IOException {
		
		/* 
		 * TODO:
		 * Should move System.getProperty("user.dir") into HookInputSet? 
		 */
		String userDir = "/home/git/org_name/repo_name.git";
		String args[] = {"oldSha", "newSha", "branchName"};
		
		HookInputSet hookInputSet = new HookInputSet(userDir, args);
		
		assertEquals(hookInputSet.getBranchName(), "branchName");
		assertEquals(hookInputSet.getOldSha(), "oldSha");
		assertEquals(hookInputSet.getNewSha(), "newSha");
		
		assertEquals(hookInputSet.getOrganizationName(), "org_name");
		assertEquals(hookInputSet.getRepositoryName(), "repo_name");
	}
}
