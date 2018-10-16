package com.gitenter.enzymark.tracefactory;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.eclipse.jgit.api.errors.CheckoutConflictException;
import org.eclipse.jgit.api.errors.GitAPIException;

import com.gitenter.enzymark.traceanalyzer.ItemTagNotUniqueException;
import com.gitenter.enzymark.traceanalyzer.TraceAnalyzerException;
import com.gitenter.enzymark.traceanalyzer.TraceableDocument;
import com.gitenter.enzymark.traceanalyzer.TraceableRepository;
import com.gitenter.gitar.GitFile;
import com.gitenter.gitar.GitFolder;
import com.gitenter.gitar.GitPath;

public class TraceableRepositoryFactory {
	
	public TraceableRepository getTraceableRepository(GitFolder gitFolder) 
			throws FileNotFoundException, CheckoutConflictException, GitAPIException, IOException, TraceAnalyzerException {
		
		TraceableRepository repository = new TraceableRepository();
		recursivelyInsertGitDocuments(repository, gitFolder);
		
		repository.refreshUpstreamAndDownstreamItems();
		return repository;
	}
	
	private void recursivelyInsertGitDocuments(TraceableRepository repository, GitFolder gitFolder) 
			throws ItemTagNotUniqueException, IOException, GitAPIException {
		
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
				
				recursivelyInsertGitDocuments(repository, subFolder);
			}
		}
	}
}
