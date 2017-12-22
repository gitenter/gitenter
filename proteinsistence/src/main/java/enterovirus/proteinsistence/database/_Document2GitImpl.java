package enterovirus.proteinsistence.database;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.IOException;
import java.util.List;

import enterovirus.proteinsistence.domain.*;
import enterovirus.gitar.*;
import enterovirus.gitar.wrap.BranchName;
import enterovirus.gitar.wrap.CommitSha;

@Repository
public class _Document2GitImpl implements _Document2Repository {

	@Autowired
	private GitSource gitSource;
	
	public _Document2Bean findDocument (File repositoryDirectory, CommitSha commitSha, String filePath) throws IOException {

		_Document2Bean document = new _Document2Bean();

		GitTextFile gitTextFile = new GitTextFile(repositoryDirectory, commitSha, filePath);
		
		int lineNumber = 1;
		for (String content : gitTextFile.getLinewiseContent()) {
			document.addLineContent(new LineContentBean(new Integer(lineNumber), content));
			++lineNumber;
		}

		return document;
	}
	
	public _Document2Bean findDocument (String ownerName, String repositoryName, BranchName branchName, String filePath) throws IOException {

		_Document2Bean document = new _Document2Bean();

		File repositoryDirectory = gitSource.getBareRepositoryDirectory(ownerName, repositoryName);
		GitTextFile gitTextFile = new GitTextFile(repositoryDirectory, branchName, filePath);
		
		int lineNumber = 1;
		for (String content : gitTextFile.getLinewiseContent()) {
			document.addLineContent(new LineContentBean(new Integer(lineNumber), content));
			++lineNumber;
		}

		return document;
	}
}
