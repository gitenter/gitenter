package enterovirus.capsid.database;

import java.io.File;
import java.io.IOException;

import enterovirus.capsid.domain.*;
import enterovirus.gitar.*;

public interface DocumentRepository {

	public DocumentBean findDocument (File repositoryDirectory, GitCommit commit, String filePath) throws IOException;
	public DocumentBean findDocument (String username, String repositoryName, GitBranch branch, String filePath) throws IOException;
}
