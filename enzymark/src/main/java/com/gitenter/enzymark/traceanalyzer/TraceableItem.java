package com.gitenter.enzymark.traceanalyzer;

import java.util.ArrayList;
import java.util.List;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@EqualsAndHashCode(exclude={"upstreamItems", "downstreamItems"})
public class TraceableItem {
	
	@Getter
	private String tag;
	
	@Getter
	private String content;
	
	@Getter
	private TraceableDocument document;
	
	@Getter
	private List<TraceableItem> upstreamItems = new ArrayList<TraceableItem>();
	
	@Getter
	private List<TraceableItem> downstreamItems = new ArrayList<TraceableItem>();
	
	/*
	 * This is for temporarily saving Used by 
	 * TraceableRepository.refreshUpstreamAndDownstreamItems() 
	 * After all the upstream and downstream items are refreshed, 
	 * this attribute has no use.
	 */
	String[] upstreamItemTags;
	
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
}
