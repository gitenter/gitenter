package enterovirus.gitar;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

import static org.junit.Assert.*;

import org.eclipse.jgit.lib.ObjectId;
import org.junit.Test;

import enterovirus.gitar.wrap.BranchName;
import enterovirus.gitar.wrap.CommitSha;

public class GitBlobTest {

	@Test
	public void test1() throws IOException {
		
		GitBlob gitBlob;
		
		File repositoryDirectory = new File("/home/beta/Workspace/enterovirus-test/one-repo-fix-commit/org/repo.git");
		String relativeFilepath = "1st-commit-file-to-be-change-in-the-2nd-commit";
		
		File commitRecordFile = new File("/home/beta/Workspace/enterovirus-test/one-repo-fix-commit/commit-sha-list.txt");
		CommitSha commitSha = new CommitSha(commitRecordFile, 1);
		gitBlob = new GitBlob(repositoryDirectory, commitSha, relativeFilepath);
		System.out.println(new String(gitBlob.getBlobContent()));
		
		BranchName branchName = new BranchName("master");
		gitBlob = new GitBlob(repositoryDirectory, branchName, relativeFilepath);
		System.out.println(new String(gitBlob.getBlobContent()));
		
		gitBlob = new GitBlob(repositoryDirectory, relativeFilepath);
		System.out.println(new String(gitBlob.getBlobContent()));
	}
		
	@Test
	public void test2() throws IOException {
		
		GitBlob gitBlob;
		
		File repositoryDirectory = new File("/home/beta/Workspace/enterovirus-test/one-commit-traceability-path/org/repo.git");
		String relativeFilepath = "document-1.md";
		
		gitBlob = new GitBlob(repositoryDirectory, relativeFilepath);
		System.out.println(new String(gitBlob.getBlobContent()));
	}
}