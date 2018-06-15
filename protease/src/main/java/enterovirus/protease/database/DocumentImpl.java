package enterovirus.protease.database;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import enterovirus.protease.domain.*;
import enterovirus.protease.source.GitSource;
import enterovirus.gitar.*;

@Repository
class DocumentImpl implements DocumentRepository {

	@Autowired private DocumentDatabaseRepository documentDbRepository;
	@Autowired private CommitRepository commitRepository;
	@Autowired private GitSource gitSource;

	public DocumentBean findById(Integer id) throws IOException, GitAPIException {
	
	Optional<DocumentBean> documents = documentDbRepository.findById(id);
	
	if (!documents.isPresent()) {
		throw new IOException ("Id is not correct!");
	}
	
	DocumentBean document = documents.get();
	updateGitMaterial(document);

	return document;
}

	public DocumentBean findByCommitIdAndRelativeFilepath(Integer commitId, String relativeFilepath) throws IOException, GitAPIException {

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
	
	public List<DocumentBean> findByCommitIdAndRelativeFilepathIn(Integer commitId, List<String> relativeFilepaths) throws IOException, GitAPIException {
	
		List<DocumentBean> documents = documentDbRepository.findByCommitIdAndRelativeFilepathIn(commitId, relativeFilepaths);
		for (DocumentBean document : documents) {
			updateGitMaterial(document);
		}
		return documents;
	}
	
	public DocumentBean findByCommitShaAndRelativeFilepath(String commitSha, String relativeFilepath) throws IOException, GitAPIException {

		List<DocumentBean> documents = documentDbRepository.findByShaChecksumHashAndRelativeFilepath(commitSha, relativeFilepath);
		
		if (documents.size() > 1) {
			throw new IOException ("Cannot locate an unique file from commitSha \""+commitSha+"\" and relativeFilepath! \""+relativeFilepath+"\"");
		}
		else if (documents.size() == 0) {
			throw new IOException ("There is no file under commitSha \""+commitSha+"\" and relativeFilepath \""+relativeFilepath+"\"!");
		}
		
		DocumentBean document = documents.get(0);
		updateGitMaterial(document);
		return document; 
	}
	
	public DocumentBean findByRepositoryIdAndBranchAndRelativeFilepath(Integer repositoryId, BranchBean branch, String relativeFilepath) throws IOException, GitAPIException {
		
		/*
		 * TODO:
		 * Should be a better way rather than query the database twice?
		 * It is pretty hard, since "branch" is not saved in database.
		 */
		CommitBean commit = commitRepository.findByRepositoryIdAndBranch(repositoryId, branch);
		return findByCommitIdAndRelativeFilepath(commit.getId(), relativeFilepath);
	}
	
	private void updateGitMaterial (DocumentBean document) throws IOException, GitAPIException {

		File repositoryDirectory = gitSource.getBareRepositoryDirectory(
				document.getCommit().getRepository().getOrganization().getName(), 
				document.getCommit().getRepository().getName());
		
		GitRepository gitRepository = GitBareRepository.getInstance(repositoryDirectory);
		GitCommit gitCommit = gitRepository.getCommit(document.getCommit().getShaChecksumHash());
		GitFile gitFile = gitCommit.getFile(document.getRelativeFilepath());
		
		document.setBlobContent(gitFile.getBlobContent());
	}
	
	public DocumentBean saveAndFlush(DocumentBean document) {
		
		return documentDbRepository.saveAndFlush(document);
	}
}
