package com.gitenter.protease.domain.traceability;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TraceableDocumentBeanTest {

	/*
	 * TODO:
	 * Should we remove the nontrivial constructor of "TraceableItemBean",
	 * and initialize "DocumentBean" only through the ORM (so
	 * "List<TraceableItemBean> traceableItems" is naturally initialized)?
	 */
	@Test
	public void testAddTraceableItem() {
		
		TraceableDocumentBean traceableDocument = new TraceableDocumentBean();
		
		TraceableItemBean traceableItem1 = new TraceableItemBean();
		traceableItem1.setTraceableDocument(traceableDocument);
		traceableItem1.setItemTag("tag-1");
		traceableItem1.setContent("content-1");
		traceableDocument.addTraceableItem(traceableItem1);
		
		TraceableItemBean traceableItem2 = new TraceableItemBean();
		traceableItem2.setTraceableDocument(traceableDocument);
		traceableItem2.setItemTag("tag-2");
		traceableItem2.setContent("content-2");
		traceableDocument.addTraceableItem(traceableItem2);
		
		traceableItem1.addDownstreamItem(traceableItem2);
		traceableItem2.addUpstreamItem(traceableItem1);
		
		assertEquals("content-1", traceableDocument.getTraceableItem("tag-1").getContent());
		assertEquals("content-2", traceableDocument.getTraceableItem("tag-2").getContent());
		assertEquals("tag-2", traceableDocument.getTraceableItem("tag-1").getDownstreamItems().get(0).getItemTag());
		assertEquals("tag-1", traceableDocument.getTraceableItem("tag-2").getUpstreamItems().get(0).getItemTag());
	}
}
