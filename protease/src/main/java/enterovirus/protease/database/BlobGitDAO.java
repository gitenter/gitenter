package enterovirus.protease.database;

import java.io.File;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import enterovirus.gitar.GitBlob;
import enterovirus.gitar.wrap.BranchName;
import enterovirus.gitar.wrap.CommitSha;
import enterovirus.protease.domain.*;
import enterovirus.protease.source.GitSource;

@Component
public class BlobGitDAO {

	@Autowired private GitSource gitSource;
	
	public BlobBean find(String organizationName, String repositoryName, CommitSha commitSha, String relativeFilepath) throws IOException {
		
		File repositoryDirectory = gitSource.getBareRepositoryDirectory(organizationName, repositoryName);
		GitBlob blob = new GitBlob(repositoryDirectory, commitSha, relativeFilepath);
		
		return new BlobBean(blob.getBlobContent(), blob.getMimeType());
	}

	public BlobBean find(String organizationName, String repositoryName, BranchName branchName, String relativeFilepath) throws IOException {

		File repositoryDirectory = gitSource.getBareRepositoryDirectory(organizationName, repositoryName);
		GitBlob blob = new GitBlob(repositoryDirectory, branchName, relativeFilepath);
		
		return new BlobBean(blob.getBlobContent(), blob.getMimeType());
	}
}
