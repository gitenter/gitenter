package enterovirus.gitar;

import java.io.File;

import org.junit.Test;

import enterovirus.gitar.wrap.TagName;

public class GitTagTest {

	@Test
	public void test() throws Exception {
		File repositoryDirectory = new File(System.getProperty("user.home"), "Workspace/enterovirus-test/long_commit_path/org/repo.git");
		GitTag gitTag = new GitTag(repositoryDirectory);
		for (TagName name : gitTag.getTagNames()) {
			System.out.println(name.getName());
		}
	}
}