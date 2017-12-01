package enterovirus.gitar;

import java.io.IOException;

import static org.junit.Assert.*;

import org.eclipse.jgit.lib.ObjectId;
import org.junit.Test;

public class GitBlobTest {

	@Test
	public void test() throws IOException {
		
		GitBlob gitBlob;
		
		gitBlob = new GitBlob("/home/beta/user1/repo1/.git", new GitCommit("c3474227d51ed985a4bf12c3099a68d6dbc11a77"), "folder_1/same-name-file");
		System.out.println(new String(gitBlob.getBlobContent()));
//		
//		gitBlob = new GitBlob("/home/beta/git/client_1/.git", "master", "folder_1/same-name-file");
//		System.out.println(new String(gitBlob.getBlobContent()));
//		
//		gitBlob = new GitBlob("/home/beta/git/client_1/.git", "folder_1/same-name-file");
//		System.out.println(new String(gitBlob.getBlobContent()));
		
		GitTextFile gitTextFile;
		
		gitTextFile = new GitTextFile("/home/beta/Workspace/enterovirus_data/user1/repo1/.git", "folder_1/same-name-file");
		System.out.println(gitTextFile.getStringContent());
	}

}