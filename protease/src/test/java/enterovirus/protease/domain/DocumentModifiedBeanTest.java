package enterovirus.protease.domain;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import enterovirus.protease.domain.*;

public class DocumentModifiedBeanTest {
	
	private DocumentModifiedBean document;

	@Before
	public void init() {
		
		CommitBean commit = new CommitBean();
		String relativeFilepath = "document.md";
		document = new DocumentModifiedBean(commit, relativeFilepath);
		
		TraceableItemBean traceableItem1 = new TraceableItemBean(document, new Integer(1), "tag-1", "content-1");
		TraceableItemBean traceableItem2 = new TraceableItemBean(document, new Integer(2), "tag-2", "content-2");
		document.addTraceableItem(traceableItem1);
		document.addTraceableItem(traceableItem2);
		
		TraceabilityMapBean traceabilityMap = new TraceabilityMapBean(document, traceableItem1, document, traceableItem2);
		traceableItem1.addDownstreamMap(traceabilityMap);
		traceableItem2.addUpstreamMap(traceabilityMap);
		document.addMapForAUpstreamItem(traceabilityMap);
		document.addMapForADownstreamItem(traceabilityMap);
		
		document.buildTraceableItemIndex();
	}
	
	@Test
	public void test() {
		
		assertEquals("content-1", document.getTraceableItem("tag-1").getContent());
		assertEquals("content-2", document.getTraceableItem("tag-2").getContent());
		assertEquals("tag-2", document.getTraceableItem("tag-1").getDownstreamPairs().get(0).getTraceableItem().getItemTag());
		assertEquals("tag-1", document.getTraceableItem("tag-2").getUpstreamPairs().get(0).getTraceableItem().getItemTag());
	}
}