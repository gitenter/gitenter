package com.gitenter.database.git;

import java.io.File;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.gitenter.protease.source.GitSource;

@Component
public class CommitGitDAO {

	@Autowired private GitSource gitSource;
	
//	public CommitValidBean loadFolderStructure (CommitValidBean commit, String[] includePaths) throws IOException {
//		
//		/*
//		 * Setup CommitValidBean.folderStructure
//		 */
//		String organizationName = commit.getRepository().getOrganization().getName();
//		String repositoryName = commit.getRepository().getName();
//		
//		File repositoryDirectory = gitSource.getBareRepositoryDirectory(organizationName, repositoryName);
//		CommitSha commitSha = new CommitSha(commit.getShaChecksumHash());
//			
//		GitFolderStructure gitFolderStructure = new GitFolderStructure(repositoryDirectory, commitSha, includePaths);
//		commit.setFolderStructure(gitFolderStructure.getFolderStructure());
//		
//		return commit;
//	}

}
