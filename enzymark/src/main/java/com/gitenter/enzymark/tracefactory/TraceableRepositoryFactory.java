package com.gitenter.enzymark.tracefactory;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.eclipse.jgit.api.errors.CheckoutConflictException;
import org.eclipse.jgit.api.errors.GitAPIException;

import com.gitenter.enzymark.traceanalyzer.ItemTagNotUniqueException;
import com.gitenter.enzymark.traceanalyzer.TraceAnalyzerException;
import com.gitenter.enzymark.traceanalyzer.TraceableFile;
import com.gitenter.enzymark.traceanalyzer.TraceableRepository;
import com.gitenter.gitar.GitFile;
import com.gitenter.gitar.GitFolder;
import com.gitenter.gitar.GitPath;
import com.gitenter.protease.domain.git.FileType;

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
			
			if (gitPath instanceof GitFile) {
				GitFile gitFile = (GitFile)gitPath;
				
				/*
				 * TODO:
				 * Should implement the better filtering conditions.
				 * 
				 * TODO:
				 * Should input the filtering condition into this method. As this package
				 * should know little about the application logic.
				 * 
				 * Probably one good way is to have an intermediate git layer
				 * in between gitar and protease, and have this `canBeDocument()`
				 * logic inside of this layer. Enzymark talk to this layer (so it
				 * completely hide `gitar`. We can also move `FileBean`/... into 
				 * this layer, so `FileBean.canBeDocument()` can automatically be
				 * used.
				 * 
				 * If later on we replace the `gitar` library to a git microservice
				 * (so the scaling part is completely hidden by the microservice,
				 * and `gitar` becomes the testing mock of that microservice),
				 * only that layer need to be changed.
				 */
				
				/*
				 * YAML (`gitenter.yml`, ...) will have fileType==null, so will not count.
				 */
				FileType fileType = FileType.fromMimeType(gitFile.getMimeType());
				if (fileType != null) {
					TraceableFile traceableFile = new TraceableFile(gitFile.getRelativePath(), fileType);
					traceableFile.parse(new String(gitFile.getBlobContent()));
					repository.addTraceableFile(traceableFile);
				}
			}
			else {
				assert gitPath instanceof GitFolder;
				GitFolder subFolder = (GitFolder)gitPath;
				
				recursivelyInsertGitDocuments(repository, subFolder);
			}
		}
	}
}
