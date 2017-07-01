package enterovirus.capsid.database;

import org.springframework.stereotype.Repository;

import enterovirus.capsid.domain.*;
import enterovirus.gitar.GitSource;

@Repository
public class TextFileGitImpl implements TextFileRepository {

	private GitSource gitSource;
	
	public TextFileBean findTextFile () {
		TextFileBean textFile = new TextFileBean();
		textFile.setContent("abcdefg");
		return textFile;
	}
}
