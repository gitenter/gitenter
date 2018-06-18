package com.gitenter.domain.git;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.gitenter.domain.git.CommitValidBean;
import com.gitenter.domain.git.DocumentBean;
import com.gitenter.domain.git.TraceableItemBean;

public class DocumentBeanTest {
	
	private DocumentBean document;

	@Before
	public void init() {
		
		CommitValidBean commit = new CommitValidBean();
		String relativeFilepath = "/fake/path/to/a/document.md";
		document = new DocumentBean(commit, relativeFilepath);
		
		TraceableItemBean traceableItem1 = new TraceableItemBean(document, "tag-1", "content-1");
		TraceableItemBean traceableItem2 = new TraceableItemBean(document, "tag-2", "content-2");
		document.addTraceableItem(traceableItem1);
		document.addTraceableItem(traceableItem2);
		
		traceableItem1.addDownstreamItem(traceableItem2);
		traceableItem2.addUpstreamItem(traceableItem1);
		
		document.buildTraceableItemIndex();
	}
	
	@Test
	public void test() {
		
		assertEquals("content-1", document.getTraceableItem("tag-1").getContent());
		assertEquals("content-2", document.getTraceableItem("tag-2").getContent());
		assertEquals("tag-2", document.getTraceableItem("tag-1").getDownstreamItems().get(0).getItemTag());
		assertEquals("tag-1", document.getTraceableItem("tag-2").getUpstreamItems().get(0).getItemTag());
	}
}