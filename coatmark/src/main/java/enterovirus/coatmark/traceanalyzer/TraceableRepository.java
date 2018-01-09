package enterovirus.coatmark.traceanalyzer;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	
	TraceableItem putIntoTraceableItem (TraceableItem item) {
		return traceableItemMap.put(item.getTag(), item);
	}
	
	public File getRepositoryDirectory() {
		return repositoryDirectory;
	}
	
	public List<TraceableDocument> getTraceableDocuments() {
		return traceableDocuments;
	}
	
	public void refreshUpstreamAndDownstreamItems () {
		
		for (Map.Entry<String,TraceableItem> entry : traceableItemMap.entrySet()) {
			TraceableItem item = entry.getValue();
			for (String upstreamItemTag : item.upstreamItemTags) {
				
				/*
				 * Rewrite the error condition using exception.
				 */
				TraceableItem upstreamItem = traceableItemMap.get(upstreamItemTag);
				if (upstreamItem == null) {
					System.out.println("Item "+item.getTag()+" is refering to upstream item "+upstreamItemTag+", but "+upstreamItemTag+" does not exist.");
				}
				
				upstreamItem.addDownstreamItem(item);
				item.addUpstreamItem(upstreamItem);
			}
		}
	}
}
