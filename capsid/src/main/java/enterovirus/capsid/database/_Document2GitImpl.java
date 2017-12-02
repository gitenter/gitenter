package enterovirus.capsid.database;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.IOException;
import java.util.List;

import enterovirus.capsid.domain.*;
import enterovirus.gitar.*;

@Repository
public class _Document2GitImpl implements _Document2Repository {

	@Autowired
	private GitSource gitSource;
	
	public Document2Bean findDocument (File repositoryDirectory, GitCommit commit, String filePath) throws IOException {

		Document2Bean document = new Document2Bean();

		GitTextFile gitTextFile = new GitTextFile(repositoryDirectory, commit, filePath);
		
		int lineNumber = 1;
		for (String content : gitTextFile.getLinewiseContent()) {
			document.addLineContent(new LineContentBean(new Integer(lineNumber), content));
			++lineNumber;
		}

		return document;
	}
	
	public Document2Bean findDocument (String username, String repositoryName, GitBranch branch, String filePath) throws IOException {

		Document2Bean document = new Document2Bean();

		File repositoryDirectory = new File(new File(new File(gitSource.getRootFolderPath(), username), repositoryName), ".git");
		GitTextFile gitTextFile = new GitTextFile(repositoryDirectory, branch, filePath);
		
		int lineNumber = 1;
		for (String content : gitTextFile.getLinewiseContent()) {
			document.addLineContent(new LineContentBean(new Integer(lineNumber), content));
			++lineNumber;
		}

		return document;
	}
}
