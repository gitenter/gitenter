package enterovirus.enzymark.traceanalyzer;

import java.util.ArrayList;
import java.util.List;

import enterovirus.enzymark.TraceableItemParser;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(exclude={"upstreamItems", "downstreamItems"})
public class TraceableItem {
	
	private String tag;
	private String content;
	
	private TraceableDocument document;
	
	private List<TraceableItem> upstreamItems = new ArrayList<TraceableItem>();
	private List<TraceableItem> downstreamItems = new ArrayList<TraceableItem>();
	
	/*
	 * This is for temporarily saving Used by 
	 * TraceableRepository.refreshUpstreamAndDownstreamItems() 
	 * After all the upstream and downstream items are refreshed, 
	 * this attribute has no use.
	 */
	String[] upstreamItemTags;

	public TraceableItem(TraceableItemParser parsingResult, TraceableDocument document) {
		this.tag = parsingResult.getTag();
		this.content = parsingResult.getContent();
		this.document = document;
		this.upstreamItemTags = parsingResult.getUpstreamItemTags();
	}
	
	TraceableItem(String tag, String content, TraceableDocument document, String[] upstreamItemTags) {
		this.tag = tag;
		this.content = content;
		this.document = document;
		this.upstreamItemTags = upstreamItemTags;
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

	public TraceableDocument getDocument() {
		return document;
	}

	public List<TraceableItem> getUpstreamItems() {
		return upstreamItems;
	}

	public List<TraceableItem> getDownstreamItems() {
		return downstreamItems;
	}
}
