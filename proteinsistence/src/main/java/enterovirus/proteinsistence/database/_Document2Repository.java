package enterovirus.proteinsistence.database;

import java.io.File;
import java.io.IOException;

import enterovirus.proteinsistence.domain.*;
import enterovirus.gitar.*;
import enterovirus.gitar.wrap.BranchName;
import enterovirus.gitar.wrap.CommitSha;

public interface _Document2Repository {

	public _Document2Bean findDocument (File repositoryDirectory, CommitSha commitSha, String filePath) throws IOException;
	public _Document2Bean findDocument (String ownerName, String repositoryName, BranchName branch, String filePath) throws IOException;
}
