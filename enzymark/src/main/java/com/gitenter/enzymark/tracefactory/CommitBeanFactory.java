package com.gitenter.enzymark.tracefactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jgit.api.errors.CheckoutConflictException;
import org.eclipse.jgit.api.errors.GitAPIException;

import com.gitenter.enzymark.traceanalyzer.TraceAnalyzerException;
import com.gitenter.enzymark.traceanalyzer.TraceableDocument;
import com.gitenter.enzymark.traceanalyzer.TraceableItem;
import com.gitenter.enzymark.traceanalyzer.TraceableRepository;
import com.gitenter.gitar.GitCommit;
import com.gitenter.protease.domain.auth.RepositoryBean;
import com.gitenter.protease.domain.git.CommitBean;
import com.gitenter.protease.domain.git.DocumentBean;
import com.gitenter.protease.domain.git.InvalidCommitBean;
import com.gitenter.protease.domain.git.TraceableItemBean;
import com.gitenter.protease.domain.git.ValidCommitBean;

public class CommitBeanFactory {

	public CommitBean getCommit(RepositoryBean repository, GitCommit gitCommit) 
			throws FileNotFoundException, CheckoutConflictException, GitAPIException, IOException {
		
		CommitBean commit;
		
		try {
			TraceableRepositoryFactory factory = new TraceableRepositoryFactory();
			TraceableRepository traceableRepository = factory.getTraceableRepository(gitCommit.getRoot());
			
			ValidCommitBean validCommit = new ValidCommitBean();
			validCommit.setFromGitCommit(gitCommit);
			
			for (TraceableDocument traceableDocument : traceableRepository.getTraceableDocuments()) {
				
				DocumentBean document = new DocumentBean();
				document.setCommit(validCommit);
				validCommit.addDocument(document);
				
				document.setRelativePath(traceableDocument.getRelativePath());
				
				Map<TraceableItem,TraceableItemBean> traceabilityIterateMap = new HashMap<TraceableItem,TraceableItemBean>();
				for (TraceableItem traceableItem : traceableDocument.getTraceableItems()) {
					
					TraceableItemBean itemBean = new TraceableItemBean();
					itemBean.setDocument(document);
					document.addTraceableItem(itemBean);
					
					itemBean.setItemTag(traceableItem.getTag());
					itemBean.setContent(traceableItem.getContent());
					
					traceabilityIterateMap.put(traceableItem, itemBean);
				}
				
				/*
				 * TODO:
				 * May need to move this part to a different method, and save `CommitBean` twice,
				 * as Hibernate seems have problem saving a complicated map of relationship.
				 */
				for (TraceableItem traceableItem : traceableDocument.getTraceableItems()) {
					
					for (TraceableItem downstreamItem : traceableItem.getDownstreamItems()) {
						traceabilityIterateMap.get(traceableItem).addDownstreamItem(traceabilityIterateMap.get(downstreamItem));
					}
					
					for (TraceableItem upstreamItem : traceableItem.getUpstreamItems()) {
						traceabilityIterateMap.get(traceableItem).addUpstreamItem(traceabilityIterateMap.get(upstreamItem));
					}
				}
				
				document.buildTraceableItemIndex();
			}
			
			commit = validCommit;
		}
		catch (TraceAnalyzerException e) {
			
			InvalidCommitBean invalidCommit = new InvalidCommitBean();
			invalidCommit.setErrorMessage(e.getMessage());
			
			invalidCommit.setRepository(repository);
			invalidCommit.setFromGitCommit(gitCommit);
			
			commit = invalidCommit;
		}

		repository.addCommit(commit);
		return commit;
	}
}
