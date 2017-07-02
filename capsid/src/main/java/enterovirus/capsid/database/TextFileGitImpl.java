package enterovirus.capsid.database;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.IOException;

import enterovirus.capsid.domain.*;
import enterovirus.gitar.GitSource;
import enterovirus.gitar.GitTextFile;

@Repository
public class TextFileGitImpl implements TextFileRepository {

	private GitSource gitSource;
	
	@Autowired
	public TextFileGitImpl (GitSource gitSource) {
		this.gitSource = gitSource;	
	}
	
	public TextFileBean findTextFile (String username, String repositoryName, String filePath) throws IOException {

		TextFileBean textFile = new TextFileBean();

		String repositoryPath = new File(new File(new File(gitSource.getRootFolderPath(), username), repositoryName), ".git").getPath();
		GitTextFile gitTextFile = new GitTextFile(repositoryPath, filePath);
		
		textFile.setContent(gitTextFile.getStringContent());
		return textFile;
	}
}
