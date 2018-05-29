package enterovirus.protease.database;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import enterovirus.protease.domain.*;
import enterovirus.protease.source.GitSource;
import enterovirus.gitar.*;
import enterovirus.gitar.temp.GitBlob;
import enterovirus.gitar.wrap.BranchName;
import enterovirus.gitar.wrap.CommitSha;

@Repository
class DocumentImpl implements DocumentRepository {

	@Autowired private DocumentDatabaseRepository documentDbRepository;
	@Autowired private CommitRepository commitRepository;
	@Autowired private GitSource gitSource;

	public DocumentBean findById(Integer id) throws IOException {
	
	Optional<DocumentBean> documents = documentDbRepository.findById(id);
	
	if (!documents.isPresent()) {
		throw new IOException ("Id is not correct!");
	}
	
	DocumentBean document = documents.get();
	updateGitMaterial(document);

	return document;
}

	public DocumentBean findByCommitIdAndRelativeFilepath(Integer commitId, String relativeFilepath) throws IOException {

		List<DocumentBean> documents = documentDbRepository.findByCommitIdAndRelativeFilepath(commitId, relativeFilepath);	
		
		/*
		 * This condition is stronger than what SQL and PL/pgSQL can define.
		 * But for a consistent and valid git input, it should be correct.
		 */
		if (documents.size() > 1) {
			throw new IOException ("Cannot locate an unique file from commitId \""+commitId+"\" and relativeFilepath! \""+relativeFilepath+"\"");
		}
		else if (documents.size() == 0) {
			throw new IOException ("There is no file under commitId \""+commitId+"\" and relativeFilepath \""+relativeFilepath+"\"!");
		}
		
		DocumentBean document = documents.get(0);
		updateGitMaterial(document);
		return document;
	}
	
	public List<DocumentBean> findByCommitIdAndRelativeFilepathIn(Integer commitId, List<String> relativeFilepaths) throws IOException {
	
		List<DocumentBean> documents = documentDbRepository.findByCommitIdAndRelativeFilepathIn(commitId, relativeFilepaths);
		for (DocumentBean document : documents) {
			updateGitMaterial(document);
		}
		return documents;
	}
	
	public DocumentBean findByCommitShaAndRelativeFilepath(CommitSha commitSha, String relativeFilepath) throws IOException {

		List<DocumentBean> documents = documentDbRepository.findByShaChecksumHashAndRelativeFilepath(commitSha.getShaChecksumHash(), relativeFilepath);
		
		if (documents.size() > 1) {
			throw new IOException ("Cannot locate an unique file from commitSha \""+commitSha.getShaChecksumHash()+"\" and relativeFilepath! \""+relativeFilepath+"\"");
		}
		else if (documents.size() == 0) {
			throw new IOException ("There is no file under commitSha \""+commitSha.getShaChecksumHash()+"\" and relativeFilepath \""+relativeFilepath+"\"!");
		}
		
		DocumentBean document = documents.get(0);
		updateGitMaterial(document);
		return document; 
	}
	
	public DocumentBean findByRepositoryIdAndBranchAndRelativeFilepath(Integer repositoryId, BranchName branchName, String relativeFilepath) throws IOException {
		
		/*
		 * TODO:
		 * Should be a better way rather than query the database twice?
		 * It is pretty hard, since "branch" is not saved in database.
		 */
		CommitBean commit = commitRepository.findByRepositoryIdAndBranch(repositoryId, branchName);
		return findByCommitIdAndRelativeFilepath(commit.getId(), relativeFilepath);
	}
	
	private void updateGitMaterial (DocumentBean document) throws IOException {
		
		String organizationName = document.getCommit().getRepository().getOrganization().getName();
		String repositoryName = document.getCommit().getRepository().getName();

		File repositoryDirectory = gitSource.getBareRepositoryDirectory(organizationName, repositoryName);
		
		CommitSha commitSha = new CommitSha(document.getCommit().getShaChecksumHash());
		String filepath = document.getRelativeFilepath();
		
		/*
		 * As we know this is a text file, so we can change the
		 * "byte[]" type to String.
		 */
		GitBlob blob = new GitBlob(repositoryDirectory, commitSha, filepath);
		document.setBlobContent(blob.getBlobContent());
	}
	
	public DocumentBean saveAndFlush(DocumentBean document) {
		
		return documentDbRepository.saveAndFlush(document);
	}
}
