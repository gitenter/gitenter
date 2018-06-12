package enterovirus.protease.database;

import java.io.IOException;
import java.util.List;

import org.eclipse.jgit.api.errors.GitAPIException;

import enterovirus.protease.domain.BranchBean;
import enterovirus.protease.domain.DocumentBean;

public interface DocumentRepository {

	public DocumentBean findById(Integer id) throws IOException, GitAPIException;
	public DocumentBean findByCommitIdAndRelativeFilepath(Integer commitId, String relativeFilepath) throws IOException, GitAPIException;
	public DocumentBean findByCommitShaAndRelativeFilepath(String commitSha, String relativeFilepath) throws IOException, GitAPIException;
	public List<DocumentBean> findByCommitIdAndRelativeFilepathIn(Integer commitId, List<String> relativeFilepaths) throws IOException, GitAPIException;
	public DocumentBean findByRepositoryIdAndBranchAndRelativeFilepath(Integer repositoryId, BranchBean branch, String relativeFilepath) throws IOException, GitAPIException ;

	public DocumentBean saveAndFlush(DocumentBean document);
}
