package enterovirus.capsid.database;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.IOException;

import enterovirus.capsid.domain.*;
import enterovirus.gitar.GitSource;
import enterovirus.gitar.GitTextFile;

@Repository
public class DocumentGitImpl implements DocumentRepository {

	private GitSource gitSource;
	
	@Autowired
	public DocumentGitImpl (GitSource gitSource) {
		this.gitSource = gitSource;	
	}
	
	public DocumentBean findDocument (String username, String repositoryName, String branchName, String filePath) throws IOException {

		DocumentBean document = new DocumentBean();

		String repositoryPath = new File(new File(new File(gitSource.getRootFolderPath(), username), repositoryName), ".git").getPath();
		GitTextFile gitTextFile = new GitTextFile(repositoryPath, branchName, filePath);
		
		int lineNumber = 1;
		for (String content : gitTextFile.getLinewiseContent()) {
			document.addLineContent(new LineContentBean(new Integer(lineNumber), content));
			++lineNumber;
		}

		return document;
	}
}