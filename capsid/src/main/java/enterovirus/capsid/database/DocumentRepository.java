package enterovirus.capsid.database;

import java.io.IOException;

import enterovirus.capsid.domain.*;

public interface DocumentRepository {

	public DocumentBean findDocument (String username, String repositoryName, String branchName, String filePath) throws IOException;
}
