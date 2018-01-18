package enterovirus.protease.database;

import java.io.IOException;
import java.util.List;

import enterovirus.gitar.wrap.*;
import enterovirus.protease.domain.*;

public interface CommitRepository {

	public CommitBean findById(Integer id) throws IOException;
	public CommitBean findByCommitSha(CommitSha commitSha) throws IOException;
	public List<CommitBean> findByCommitShaIn(List<CommitSha> commitShas);
	
	public CommitBean findByRepositoryIdAndBranch(Integer repositoryId, BranchName branchName) throws IOException;
	
	public CommitBean saveAndFlush(CommitBean commit);
}
