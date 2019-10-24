package com.gitenter.enzymark.traceanalyzer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Getter;

/*
 * TODO:
 * As most (right now all) usage cases right now need to start from a git
 * historical state, this package should starts with git GitRepository and GitFile.
 */
public class TraceableRepository {
	
	@Getter
	private List<TraceableFile> traceableFiles = new ArrayList<TraceableFile>();
	
	private Map<String,TraceableItem> traceableItemMap = new HashMap<String,TraceableItem>();

	public TraceableRepository() {

	}

	public void addTraceableFile(TraceableFile document) throws ItemTagNotUniqueException {

		traceableFiles.add(document);
		
		for (TraceableItem traceableItem : document.getTraceableItems()) {
			putIntoTraceableItem(traceableItem);
		}
	}
	
	private void putIntoTraceableItem(TraceableItem item) throws ItemTagNotUniqueException {
		
		if (traceableItemMap.containsKey(item.getTag())) {
			TraceableItem originalItem = traceableItemMap.get(item.getTag());
			throw new ItemTagNotUniqueException(item.getTag(), originalItem.getDocument(), item.getDocument());
		}
		traceableItemMap.put(item.getTag(), item);
	}
	
	public void refreshUpstreamAndDownstreamItems() throws UpstreamTagNotExistException {
		
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
