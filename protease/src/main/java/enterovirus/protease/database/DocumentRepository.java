package enterovirus.protease.database;

import java.io.IOException;

import enterovirus.protease.domain.*;

public interface DocumentRepository {

	public DocumentBean findById(Integer id) throws IOException;
	public DocumentBean findByCommitIdAndRelativeFilepath(Integer commitId, String relativeFilepath) throws IOException;
	public DocumentBean findByRepositoryIdAndBranchAndRelativeFilepath(Integer repositoryId, String branch, String relativeFilepath) throws IOException;
	public DocumentBean findByRepositoryIdAndRelativeFilepath(Integer repositoryId, String relativeFilepath) throws IOException;
}