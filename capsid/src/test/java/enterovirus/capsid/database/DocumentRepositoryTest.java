package enterovirus.capsid.database;

import org.junit.runner.RunWith;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import enterovirus.capsid.domain.*;
import enterovirus.gitar.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DocumentRepositoryTest {

	@Autowired
	private DocumentRepository repository;

	@Test
	public void test1() throws Exception {
		
		String repositoryPath = "/home/beta/user1/repo1/.git";
		String commitSha = "c3474227d51ed985a4bf12c3099a68d6dbc11a77";
		String folderPath = "folder_1/same-name-file";
		System.out.println("Repository Path: "+repositoryPath);
		System.out.println("Commit SHA: "+commitSha);
		System.out.println("folderPath: "+folderPath);

		DocumentBean document = repository.findDocument(repositoryPath, new GitCommit(commitSha), folderPath);
		
		System.out.println(document.getLineContents());
		for (LineContentBean content : document.getLineContents()) {
			System.out.println(content.getContent());
		}
	}
	
	@Test
	public void test2() throws Exception {

		String username = "user1";
		String repositoryName = "repo1";
		String branchName = "master";
		String folderPath = "folder_1/same-name-file";
		System.out.println("Username: "+username);
		System.out.println("Repository Name: "+repositoryName);
		System.out.println("Branch Name: "+branchName);
		System.out.println("folderPath: "+folderPath);
		
		DocumentBean document = repository.findDocument(username, repositoryName, new GitBranch(branchName), folderPath);
		System.out.println(document.getLineContents());
		for (LineContentBean content : document.getLineContents()) {
			System.out.println(content.getContent());
		}
	}
}
