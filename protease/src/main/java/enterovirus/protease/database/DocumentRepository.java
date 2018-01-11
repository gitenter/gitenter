package enterovirus.protease.database;

import java.io.IOException;

import enterovirus.gitar.wrap.BranchName;
import enterovirus.protease.domain.*;

public interface DocumentRepository {

	public DocumentBean findById(Integer id) throws IOException;
	public DocumentBean findByCommitIdAndRelativeFilepath(Integer commitId, String relativeFilepath) throws IOException;
	public DocumentBean findByRepositoryIdAndBranchAndRelativeFilepath(Integer repositoryId, BranchName branchName, String relativeFilepath) throws IOException;

	public DocumentBean saveAndFlush(DocumentBean document);
}
