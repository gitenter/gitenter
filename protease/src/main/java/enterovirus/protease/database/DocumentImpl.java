package enterovirus.protease.database;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import enterovirus.protease.domain.*;
import enterovirus.gitar.*;
import enterovirus.gitar.wrap.CommitSha;

@Repository
class DocumentImpl implements DocumentRepository {

	@Autowired private DocumentDatabaseRepository documentDbRepository;
	@Autowired private DocumentModifiedDatabaseRepository documentMoDbRepository;
	@Autowired private DocumentUnmodifiedDatabaseRepository documentUnmoDbRepository;
	@Autowired private CommitRepository commitRepository;
	@Autowired private GitSource gitSource;

	public DocumentBean findById(Integer id) throws IOException {
	
	Optional<DocumentBean> documents = documentDbRepository.findById(id);
	
	if (!documents.isPresent()) {
		throw new IOException ("Id is not correct!");
	}
	
	DocumentBean document = documents.get();
	
	if (document instanceof DocumentModifiedBean) {
		updateGitMaterial((DocumentModifiedBean)document);
	}
	else {
		updateGitMaterial(((DocumentUnmodifiedBean)document).getOriginalDocument());
	}
	return document;
}

	public DocumentBean findByCommitIdAndRelativeFilepath(Integer commitId, String relativeFilepath) throws IOException {

		List<DocumentModifiedBean> unmoDocuments = documentMoDbRepository.findByCommitIdAndRelativeFilepath(commitId, relativeFilepath);	
		List<DocumentUnmodifiedBean> moDocuments = documentUnmoDbRepository.findByCommitIdAndRelativeFilepath(commitId, relativeFilepath);
		
		/*
		 * This condition is stronger than what SQL and PL/pgSQL can define.
		 * But for a consistent and valid git input, it should be correct.
		 */
		if (unmoDocuments.size() + moDocuments.size() > 1) {
			throw new IOException ("Cannot locate an unique file from commitId and relativeFilepath!");
		}
		else if (unmoDocuments.size() + moDocuments.size() == 0) {
			throw new IOException ("There is no file under this commitId and relativeFilepath!");
		}
		
		DocumentBean document;
		if (unmoDocuments.size() == 1) {
			document = unmoDocuments.get(0);
			updateGitMaterial((DocumentModifiedBean)document);
		}
		else {
			document = moDocuments.get(0);
			updateGitMaterial(((DocumentUnmodifiedBean)document).getOriginalDocument());
		}
		
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
	
	private void updateGitMaterial (DocumentModifiedBean document) throws IOException {
		
		String organizationName = document.getCommit().getRepository().getOrganization().getName();
		String repositoryName = document.getCommit().getRepository().getName();

		File repositoryDirectory = gitSource.getBareRepositoryDirectory(organizationName, repositoryName);
		
		CommitSha commitSha = new CommitSha(document.getCommit().getShaChecksumHash());
		String filepath = document.getRelativeFilepath();
		
		GitBlob blob = new GitBlob(repositoryDirectory, commitSha, filepath);
		
		/*
		 * TODO: 
		 * Split by "newline" which is compatible to Windows
		 * or Linux formats.
		 * 
		 * TODO:
		 * Notice that blob which is not text file (e.g. images) also need to 
		 * be loaded and displayed in some way. Should later design a better
		 * and compitable way to handle both.
		 */
		int lineNumber = 1;
		for (String content : new String(blob.getBlobContent()).split("\n")) {
			document.addLineContent(new LineContentBean(new Integer(lineNumber), content));
			++lineNumber;
		}
	}
	
	public DocumentBean saveAndFlush(DocumentBean document) {
		
		return documentDbRepository.saveAndFlush(document);
	}
}
