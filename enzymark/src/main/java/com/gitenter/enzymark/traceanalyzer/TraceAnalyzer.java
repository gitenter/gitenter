package com.gitenter.enzymark.traceanalyzer;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.eclipse.jgit.api.errors.CheckoutConflictException;
import org.eclipse.jgit.api.errors.GitAPIException;

import com.gitenter.gitar.GitFile;
import com.gitenter.gitar.GitFolder;
import com.gitenter.gitar.GitHistoricalFolder;
import com.gitenter.gitar.GitLocalFolder;
import com.gitenter.gitar.GitNormalRepository;
import com.gitenter.gitar.GitPath;
import com.gitenter.gitar.GitRepository;

public class TraceAnalyzer {

	public static TraceableRepository analyzeGitWorkspace(GitNormalRepository gitRepository) throws FileNotFoundException, CheckoutConflictException, GitAPIException, IOException, ItemTagNotUniqueException {
		
		TraceableRepository repository = new TraceableRepository(gitRepository.getDirectory());
		
		GitLocalFolder root = gitRepository.getCurrentBranch().checkoutTo().getRoot();
		insertDocumentsIntoRepository(repository, root);
		
		return repository;
	}
	
	public static TraceableRepository analyzeGitCommit(GitRepository gitRepository, String sha) throws FileNotFoundException, CheckoutConflictException, GitAPIException, IOException, ItemTagNotUniqueException {
		
		TraceableRepository repository = new TraceableRepository(gitRepository.getDirectory());
		
		GitHistoricalFolder root = gitRepository.getCommit(sha).getRoot();
		insertDocumentsIntoRepository(repository, root);
		
		return repository;
	}
	
	private static void insertDocumentsIntoRepository(TraceableRepository repository, GitFolder gitFolder) throws ItemTagNotUniqueException, IOException, GitAPIException {
		
		for (GitPath gitPath : gitFolder.ls()) {
			
			/*
			 * TODO:
			 * Should implement the filtering conditions that which file we would like
			 * to analysis the traceable items.
			 */
			if (gitPath instanceof GitFile) {
				GitFile gitFile = (GitFile)gitPath;
				
				TraceableDocument document = new TraceableDocument(gitFile.getRelativePath());
				document.parse(new String(gitFile.getBlobContent()));
				repository.addTraceableDocument(document);
			}
			else {
				assert gitPath instanceof GitFolder;
				GitFolder subFolder = (GitFolder)gitPath;
				
				insertDocumentsIntoRepository(repository, subFolder);
			}
		}
	}
}
