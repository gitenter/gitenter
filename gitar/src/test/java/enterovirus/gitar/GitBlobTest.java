package enterovirus.gitar;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import enterovirus.gitar.wrap.BranchName;
import enterovirus.gitar.wrap.CommitSha;

public class GitBlobTest {

	@Test
	public void test1() throws IOException {
		
		GitBlob gitBlob;
		
		File repositoryDirectory = new File("/home/beta/Workspace/enterovirus-test/one_repo_fix_commit/org/repo.git");
		String relativeFilepath = "1st-commit-file-to-be-change-in-the-2nd-commit";
		
		File commitRecordFile = new File("/home/beta/Workspace/enterovirus-test/one_repo_fix_commit/commit-sha-list.txt");
		CommitSha commitSha = new CommitSha(commitRecordFile, 1);
		gitBlob = new GitBlob(repositoryDirectory, commitSha, relativeFilepath);
		System.out.println("Content: "+new String(gitBlob.getBlobContent()));
		
		BranchName branchName = new BranchName("master");
		gitBlob = new GitBlob(repositoryDirectory, branchName, relativeFilepath);
		System.out.println("Content: "+new String(gitBlob.getBlobContent()));
	}
		
	@Test
	public void test2() throws IOException {
		
		GitBlob gitBlob;
		
		File repositoryDirectory = new File("/home/beta/Workspace/enterovirus-test/mime_types/org/repo.git");
		BranchName branchName = new BranchName("master");
		
		gitBlob = new GitBlob(repositoryDirectory, branchName, "sample.png");
		assertEquals(gitBlob.getMimeType(), "image/png");
		
		gitBlob = new GitBlob(repositoryDirectory, branchName, "sample.jpg");
		assertEquals(gitBlob.getMimeType(), "image/jpeg");

		gitBlob = new GitBlob(repositoryDirectory, branchName, "sample.gif");
		assertEquals(gitBlob.getMimeType(), "image/gif");

		gitBlob = new GitBlob(repositoryDirectory, branchName, "sample.html");
		assertEquals(gitBlob.getMimeType(), "text/html");

		gitBlob = new GitBlob(repositoryDirectory, branchName, "sample.md");
		System.out.println("sample.md MIME type: "+gitBlob.getMimeType());

		gitBlob = new GitBlob(repositoryDirectory, branchName, "sample.pdf");
		System.out.println("sample.pdf MIME type: "+gitBlob.getMimeType());

		gitBlob = new GitBlob(repositoryDirectory, branchName, "Sample.java");
		System.out.println("sample.pdf MIME type: "+gitBlob.getMimeType());
		
	}
}