package enterovirus.capsid.database;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import enterovirus.capsid.domain.*;
import enterovirus.gitar.*;
import enterovirus.gitar.identification.CommitSha;

@Repository
public class DocumentGitImpl implements DocumentRepository {

	@Autowired private DocumentDatabaseRepository repository;
	@Autowired private GitSource gitSource;
	
	public List<DocumentBean> findById(Integer id) throws IOException {
		
		List<DocumentBean> documents = repository.findById(id);
		
		for (DocumentBean document : documents) {
			
			String organizationName = document.getCommit().getRepository().getOrganization().getName();
			String repositoryName = document.getCommit().getRepository().getName();

			File repositoryDirectory = gitSource.getRepositoryDirectory(organizationName, repositoryName);
			
			CommitSha commitSha = new CommitSha(document.getCommit().getShaChecksumHash());
			String filepath = document.getRelativeFilepath();
			
			GitTextFile gitTextFile = new GitTextFile(repositoryDirectory, commitSha, filepath);
			
			int lineNumber = 1;
			for (String content : gitTextFile.getLinewiseContent()) {
				document.addLineContent(new LineContentBean(new Integer(lineNumber), content));
				++lineNumber;
			}
		}
		
		return documents;
	}
}
