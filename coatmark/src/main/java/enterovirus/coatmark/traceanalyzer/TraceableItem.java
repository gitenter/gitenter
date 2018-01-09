package enterovirus.coatmark.traceanalyzer;

import java.util.ArrayList;
import java.util.List;

import enterovirus.coatmark.TraceableItemParser;

public class TraceableItem {
	
	private String tag;
	private String content;
	
	private List<TraceableItem> upstreamItems = new ArrayList<TraceableItem>();
	private List<TraceableItem> downstreamItems = new ArrayList<TraceableItem>();
	
	/*
	 * This is for temporarily saving Used by 
	 * TraceableRepository.refreshUpstreamAndDownstreamItems() 
	 * After all the upstream and downstream items are refreshed, 
	 * this attribute has no use.
	 */
	List<String> upstreamItemTags = new ArrayList<String>();

	public TraceableItem(TraceableItemParser parsingResult) {
		this.tag = parsingResult.getTag();
		this.content = parsingResult.getContent();
		this.upstreamItemTags = parsingResult.getUpstreamItemTags();
	}
	
	/*
	 * Used by TraceableRepository.refreshUpstreamAndDownstreamItems()
	 */
	boolean addUpstreamItem (TraceableItem item) {
		return upstreamItems.add(item);
	}
	
	/*
	 * Used by TraceableRepository.refreshUpstreamAndDownstreamItems()
	 */
	boolean addDownstreamItem (TraceableItem item) {
		return downstreamItems.add(item);
	}

	public String getTag() {
		return tag;
	}

	public String getContent() {
		return content;
	}

	public List<TraceableItem> getUpstreamItems() {
		return upstreamItems;
	}

	public List<TraceableItem> getDownstreamItems() {
		return downstreamItems;
	}
}
