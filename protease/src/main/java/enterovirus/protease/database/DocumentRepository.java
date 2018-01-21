package enterovirus.protease.database;

import java.io.IOException;
import java.util.List;

import enterovirus.gitar.wrap.BranchName;
import enterovirus.gitar.wrap.CommitSha;
import enterovirus.protease.domain.*;

public interface DocumentRepository {

	public DocumentBean findById(Integer id) throws IOException;
	public DocumentBean findByCommitIdAndRelativeFilepath(Integer commitId, String relativeFilepath) throws IOException;
	public DocumentBean findByCommitShaAndRelativeFilepath(CommitSha commitSha, String relativeFilepath) throws IOException;
	public List<DocumentBean> findByCommitIdAndRelativeFilepathIn(Integer commitId, List<String> relativeFilepaths) throws IOException;
	public DocumentBean findByRepositoryIdAndBranchAndRelativeFilepath(Integer repositoryId, BranchName branchName, String relativeFilepath) throws IOException;

	public DocumentBean saveAndFlush(DocumentBean document);
}
