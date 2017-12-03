package enterovirus.capsid.database;

import java.io.File;
import java.io.IOException;

import enterovirus.capsid.domain.*;
import enterovirus.gitar.*;

public interface _Document2Repository {

	public _Document2Bean findDocument (File repositoryDirectory, GitCommit commit, String filePath) throws IOException;
	public _Document2Bean findDocument (String ownerName, String repositoryName, GitBranch branch, String filePath) throws IOException;
}
