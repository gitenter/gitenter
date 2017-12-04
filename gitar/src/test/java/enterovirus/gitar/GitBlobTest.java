package enterovirus.gitar;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.*;

import org.eclipse.jgit.lib.ObjectId;
import org.junit.Test;

public class GitBlobTest {

	@Test
	public void test() throws IOException {
		
		GitBlob gitBlob;
		
		File repositoryDirectory = new File("/home/beta/Workspace/enterovirus_data/user1/repo1/.git");
		GitCommitSha commitSha = new GitCommitSha("ff728f5674201025b9fc4ea76a0adde3323fb9fb"); 
		
		gitBlob = new GitBlob(repositoryDirectory, commitSha, "folder_1/same-name-file");
		System.out.println(new String(gitBlob.getBlobContent()));
//		
//		gitBlob = new GitBlob("/home/beta/git/client_1/.git", "master", "folder_1/same-name-file");
//		System.out.println(new String(gitBlob.getBlobContent()));
//		
//		gitBlob = new GitBlob("/home/beta/git/client_1/.git", "folder_1/same-name-file");
//		System.out.println(new String(gitBlob.getBlobContent()));
		
		GitTextFile gitTextFile;
		
		gitTextFile = new GitTextFile(repositoryDirectory, "folder_1/same-name-file");
		System.out.println(gitTextFile.getStringContent());
	}

}