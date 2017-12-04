package enterovirus.capsid.database;

import java.io.File;
import java.io.IOException;

import enterovirus.capsid.domain.*;
import enterovirus.gitar.*;
import enterovirus.gitar.identification.GitBranchName;
import enterovirus.gitar.identification.GitCommitSha;

public interface _Document2Repository {

	public _Document2Bean findDocument (File repositoryDirectory, GitCommitSha commitSha, String filePath) throws IOException;
	public _Document2Bean findDocument (String ownerName, String repositoryName, GitBranchName branch, String filePath) throws IOException;
}
