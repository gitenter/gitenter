package enterovirus.capsid.database;

import java.io.File;
import java.io.IOException;

import enterovirus.capsid.domain.*;
import enterovirus.gitar.*;
import enterovirus.gitar.identification.BranchName;
import enterovirus.gitar.identification.CommitSha;

public interface _Document2Repository {

	public _Document2Bean findDocument (File repositoryDirectory, CommitSha commitSha, String filePath) throws IOException;
	public _Document2Bean findDocument (String ownerName, String repositoryName, BranchName branch, String filePath) throws IOException;
}
