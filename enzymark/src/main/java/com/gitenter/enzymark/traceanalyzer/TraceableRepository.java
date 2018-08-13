package com.gitenter.enzymark.traceanalyzer;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 * TODO:
 * As most (right now all) usage cases right now need to start from a git
 * historical state, this package should starts with git GitRepository and GitFile.
 */
public class TraceableRepository {
	
	File repositoryDirectory;
	
	private List<TraceableDocument> traceableDocuments = new ArrayList<TraceableDocument>();
	private Map<String,TraceableItem> traceableItemMap = new HashMap<String,TraceableItem>();

	public TraceableRepository(File repositoryDirectory) {
		this.repositoryDirectory = repositoryDirectory;
	}

	public boolean addTraceableDocument (TraceableDocument document) {
		return traceableDocuments.add(document);
	}
	
	TraceableItem putIntoTraceableItem (TraceableItem item) throws ItemTagNotUniqueException {
		
		if (traceableItemMap.containsKey(item.getTag())) {
			TraceableItem originalItem = traceableItemMap.get(item.getTag());
			throw new ItemTagNotUniqueException(item.getTag(), originalItem.getDocument(), item.getDocument());
		}
		return traceableItemMap.put(item.getTag(), item);
	}
	
	public File getRepositoryDirectory() {
		return repositoryDirectory;
	}
	
	public List<TraceableDocument> getTraceableDocuments() {
		return traceableDocuments;
	}
	
	public void refreshUpstreamAndDownstreamItems () throws UpstreamTagNotExistException {
		
		for (Map.Entry<String,TraceableItem> entry : traceableItemMap.entrySet()) {
			TraceableItem item = entry.getValue();
			for (String upstreamItemTag : item.upstreamItemTags) {
				
				/*
				 * Rewrite the error condition using exception.
				 */
				TraceableItem upstreamItem = traceableItemMap.get(upstreamItemTag);
				if (upstreamItem == null) {
					throw new UpstreamTagNotExistException(item.getTag(), upstreamItemTag, item.getDocument());
				}
				
				upstreamItem.addDownstreamItem(item);
				item.addUpstreamItem(upstreamItem);
			}
		}
	}
}
