package enterovirus.gihook.postreceive.traceanalyzer;

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
				TraceableItem upstreamItem = traceableItemMap.get(upstreamItemTag);
				upstreamItem.addDownstreamItem(item);
				item.addUpstreamItem(upstreamItem);
			}
		}
	}
}