package enterovirus.proteinsistence.database;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import enterovirus.proteinsistence.domain.*;
import enterovirus.gitar.*;
import enterovirus.gitar.wrap.CommitSha;

@Repository
class DocumentGitImpl implements DocumentRepository {

	@Autowired private DocumentDatabaseRepository repository;
	@Autowired private CommitRepository commitRepository;
	@Autowired private GitSource gitSource;
	
	public DocumentBean findById(Integer id) throws IOException {
		
		List<DocumentBean> documents = repository.findById(id);
		
		if (documents.size() == 0) {
			throw new IOException ("Id is not correct!");
		}
		if (documents.size() > 1) {
			throw new IOException ("Id is not unique!");
		}
		
		DocumentBean document = documents.get(0);
		updateGitMaterial(document);
		return document;
	}

	public DocumentBean findByCommitIdAndRelativeFilepath(Integer commitId, String relativeFilepath) throws IOException {
		
		List<DocumentBean> documents = repository.findByCommitIdAndRelativeFilepath(commitId, relativeFilepath);
		
		/*
		 * TODO: 
		 * Try to add some unique condition in the relative place of the database.
		 * It is kind of difficult because we are working on a SQL VIEW at this moment.
		 */
		if (documents.size() == 0) {
			throw new IOException ("CommitId and/or filepath is not correct!");
		}
		if (documents.size() > 1) {
			throw new IOException ("CommitId and/or filepath is not unique!");
		}
		
		DocumentBean document = documents.get(0);
		updateGitMaterial(document);
		return document;
	}
	
	public DocumentBean findByRepositoryIdAndBranchAndRelativeFilepath(Integer repositoryId, String branch, String relativeFilepath) throws IOException {
		
		/*
		 * TODO:
		 * Should be a better way rather than query the database twice?
		 */
		CommitBean commit = commitRepository.findByRepositoryIdAndBranch(repositoryId, branch);
		return findByCommitIdAndRelativeFilepath(commit.getId(), relativeFilepath);
	}
	
	public DocumentBean findByRepositoryIdAndRelativeFilepath(Integer repositoryId, String relativeFilepath) throws IOException {
		return findByRepositoryIdAndBranchAndRelativeFilepath(repositoryId, "master", relativeFilepath);
	}
	
	private DocumentBean updateGitMaterial (DocumentBean document) throws IOException {
		
		String organizationName = document.getCommit().getRepository().getOrganization().getName();
		String repositoryName = document.getCommit().getRepository().getName();

		File repositoryDirectory = gitSource.getBareRepositoryDirectory(organizationName, repositoryName);
		
		CommitSha commitSha = new CommitSha(document.getCommit().getShaChecksumHash());
		String filepath = document.getRelativeFilepath();
		
		GitTextFile gitTextFile = new GitTextFile(repositoryDirectory, commitSha, filepath);
		
		int lineNumber = 1;
		for (String content : gitTextFile.getLinewiseContent()) {
			document.addLineContent(new LineContentBean(new Integer(lineNumber), content));
			++lineNumber;
		}
		
		return document;
	}
}
