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

public class GitDocumentTest {

	@Test
	public void test() throws IOException {
		
		GitDocument gitDocument;
		
		File repositoryDirectory = new File("/home/beta/Workspace/enterovirus-test/one-commit-traceability-path/org/repo.git");
		String relativeFilepath = "document-1.md";
		
		gitDocument = new GitDocument(repositoryDirectory, relativeFilepath);
		System.out.println(new String(gitDocument.getBlobContent()));
	}
}