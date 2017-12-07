package enterovirus.capsid.database;

import org.junit.runner.RunWith;

import java.io.File;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import enterovirus.capsid.domain.*;
import enterovirus.gitar.*;
import enterovirus.gitar.wrap.BranchName;
import enterovirus.gitar.wrap.CommitSha;

@RunWith(SpringRunner.class)
@SpringBootTest
public class _Document2RepositoryTest {

	@Autowired
	private _Document2Repository repository;

	@Test
	public void test1() throws Exception {
		
		System.out.println("======================================");
		
		File repositoryDirectory = new File("/home/beta/Workspace/enterovirus_data/user1/repo1/.git");
		CommitSha commit = new CommitSha("ff728f5674201025b9fc4ea76a0adde3323fb9fb");
		String folderPath = "folder_1/same-name-file";
		
		System.out.println("Repository Path: "+repositoryDirectory.getPath());
		System.out.println("Commit SHA: "+commit.getShaChecksumHash());
		System.out.println("folderPath: "+folderPath);

		_Document2Bean document = repository.findDocument(repositoryDirectory, commit, folderPath);
		
		System.out.println(document.getLineContents());
		for (LineContentBean content : document.getLineContents()) {
			System.out.println(content.getContent());
		}
	}
	
	@Test
	public void test2() throws Exception {
		
		System.out.println("======================================");

		String username = "user1";
		String repositoryName = "repo1";
		String branchName = "master";
		String folderPath = "folder_1/same-name-file";
		
		System.out.println("Username: "+username);
		System.out.println("Repository Name: "+repositoryName);
		System.out.println("Branch Name: "+branchName);
		System.out.println("folderPath: "+folderPath);
		
		_Document2Bean document = repository.findDocument(username, repositoryName, new BranchName(branchName), folderPath);
		System.out.println(document.getLineContents());
		for (LineContentBean content : document.getLineContents()) {
			System.out.println(content.getContent());
		}
	}
}
