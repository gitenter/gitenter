package enterovirus.capsid.database;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import enterovirus.capsid.domain.*;
import enterovirus.gitar.*;

@Repository
public class DocumentImpl implements DocumentRepository {

	@Autowired
	private DocumentDatabaseRepository repository;
	
	@Autowired
	private GitSource gitSource;
	
	public List<DocumentBean> findById(Integer id) throws IOException {
		
		List<DocumentBean> documents = repository.findById(id);
		
		for (DocumentBean document : documents) {
			
			String organizationName = document.getCommit().getRepository().getOrganization().getName();
			String repositoryName = document.getCommit().getRepository().getName();
			/**
			 * TODO:
			 * This includes the defined folder structure. Move this definition 
			 * to a general function.
			 */
			File repositoryDirectory = new File(new File(new File(gitSource.getRootFolderPath(), organizationName), repositoryName), ".git");
			
			GitCommit commit = new GitCommit(document.getCommit().getShaChecksumHash());
			String filepath = document.getFilepath();
			
			GitTextFile gitTextFile = new GitTextFile(repositoryDirectory, commit, filepath);
			
			int lineNumber = 1;
			for (String content : gitTextFile.getLinewiseContent()) {
				document.addLineContent(new LineContentBean(new Integer(lineNumber), content));
				++lineNumber;
			}
		}
		
		return documents;
	}
}
