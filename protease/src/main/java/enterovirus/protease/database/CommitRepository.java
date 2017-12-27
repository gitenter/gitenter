package enterovirus.protease.database;

import java.io.IOException;

import enterovirus.protease.domain.*;

public interface CommitRepository {

	public CommitBean findById(Integer id) throws IOException;
	public CommitBean findByShaChecksumHash(String shaChecksumHash) throws IOException;
	public CommitBean findByRepositoryIdAndBranch(Integer repositoryId, String branchName) throws IOException;
	public CommitBean findByRepositoryId(Integer repositoryId) throws IOException;
	
	public CommitBean saveAndFlush(CommitBean commit);
}
