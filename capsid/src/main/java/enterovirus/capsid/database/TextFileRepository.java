package enterovirus.capsid.database;

import java.io.IOException;

import enterovirus.capsid.domain.*;

public interface TextFileRepository {

	public TextFileBean findTextFile (String username, String repositoryName, String filePath) throws IOException;
}
