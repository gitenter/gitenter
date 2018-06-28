package com.gitenter.dao.git;

import java.io.File;
import java.io.IOException;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.gitenter.domain.git.DocumentBean;
import com.gitenter.domain.git.FileBean;
import com.gitenter.gitar.GitBareRepository;
import com.gitenter.gitar.GitCommit;
import com.gitenter.gitar.GitFile;
import com.gitenter.gitar.GitRepository;
import com.gitenter.gitar.util.GitPlaceholder;
import com.gitenter.gitar.util.GitProxyPlaceholder;
import com.gitenter.protease.source.GitSource;

public class FilePlaceholder {

	/*
	 * TODO:
	 * Cannot autowired "gitSource", because "FilePlaceholder" will be used in bean,
	 * rather than itself be chain-autowired under some Spring application setup.
	 * 
	 * So when using, will complain gitSource == null.
	 */
	@Autowired private GitSource gitSource;
	
	public interface BlobContentPlaceholder extends GitPlaceholder<byte[]> {
		byte[] get() throws IOException, GitAPIException;
	}
	
	public class ProxyBlobContentPlaceholder extends GitProxyPlaceholder<byte[]> implements BlobContentPlaceholder {

		private GitFile gitFile;
		
		public ProxyBlobContentPlaceholder(GitFile gitFile) {
			this.gitFile = gitFile;
		}

		/*
		 * Cannot load "gitFile" in the constructor from "DocumentBean", because when
		 * use it as a default constructor from "DocumentBean", it cannot raise exception.
		 */
		private DocumentBean document;
		
		public ProxyBlobContentPlaceholder(DocumentBean document) {
			this.document = document;
		}

		@Override
		protected byte[] getReal() throws IOException, GitAPIException {
			
			/*
			 * TODO:
			 * Replace this "if" with two classes implements the same interface 
			 * "BlobContentPlaceholder".
			 */
			if (gitFile == null) {	
				assert document != null;
				
				File repositoryDirectory = gitSource.getBareRepositoryDirectory(
						document.getCommit().getRepository().getOrganization().getName(), 
						document.getCommit().getRepository().getName());
				
				GitRepository gitRepository = GitBareRepository.getInstance(repositoryDirectory);
				GitCommit gitCommit = gitRepository.getCommit(document.getCommit().getSha());
				gitFile = gitCommit.getFile(document.getRelativePath());
				
				/*
				 * TODO:
				 * Then how to setup "DocumentBean.name"? Should we have another placeholder
				 * for that, but then the two placeholders cannot share the same gitFile query...
				 */
			}
			return gitFile.getBlobContent();
		}
	}
}
