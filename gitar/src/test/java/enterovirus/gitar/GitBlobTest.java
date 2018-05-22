package enterovirus.gitar;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import enterovirus.gitar.wrap.BranchName;
import enterovirus.gitar.wrap.CommitSha;
import enterovirus.gitar.wrap.TagName;

public class GitBlobTest {
		
	@Test
	public void test1() throws IOException {
		
		GitBlob gitBlob;
		
		File repositoryDirectory = new File(System.getProperty("user.home"), "Workspace/enterovirus-test/one_repo_fix_commit/org/repo.git");
		String relativeFilepath = "1st-commit-file-to-be-change-in-the-2nd-commit";
		
		File commitRecordFile = new File(System.getProperty("user.home"), "Workspace/enterovirus-test/one_repo_fix_commit/commit-sha-list.txt");
		CommitSha commitSha = new CommitSha(commitRecordFile, 1);
		gitBlob = new GitBlob(repositoryDirectory, commitSha, relativeFilepath);
		System.out.println("Content: "+new String(gitBlob.getBlobContent()));
		
		BranchName branchName = new BranchName("master");
		gitBlob = new GitBlob(repositoryDirectory, branchName, relativeFilepath);
		System.out.println("Content: "+new String(gitBlob.getBlobContent()));

		TagName tagName = new TagName("first-commit");
		gitBlob = new GitBlob(repositoryDirectory, tagName, relativeFilepath);
		System.out.println("Content: "+new String(gitBlob.getBlobContent()));
	}
		
	@Test
	public void test2() throws IOException {
		
		GitBlob gitBlob;
		
		File repositoryDirectory = new File(System.getProperty("user.home"), "Workspace/enterovirus-test/mime_types/org/repo.git");
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
		assertEquals(gitBlob.getMimeType(), "text/markdown");
		
		gitBlob = new GitBlob(repositoryDirectory, branchName, "sample.pdf");
		assertEquals(gitBlob.getMimeType(), "application/pdf");

		gitBlob = new GitBlob(repositoryDirectory, branchName, "Sample.java");
		assertEquals(gitBlob.getMimeType(), "text/plain");
	}
}