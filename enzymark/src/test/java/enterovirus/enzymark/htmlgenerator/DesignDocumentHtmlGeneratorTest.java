package enterovirus.enzymark.htmlgenerator;

import org.junit.Before;
import org.junit.Test;

import enterovirus.enzymark.htmlgenerator.DesignDocumentHtmlGenerator;
import enterovirus.protease.domain.*;

public class DesignDocumentHtmlGeneratorTest {
	
	private DocumentModifiedBean document;

	@Before
	public void init() {
		
		CommitBean commit = new CommitBean();
		String relativeFilepath = "document.md";
		document = new DocumentModifiedBean(commit, relativeFilepath);
		
		TraceableItemBean traceableItem1 = new TraceableItemBean(document, "tag-1", "content-1");
		TraceableItemBean traceableItem2 = new TraceableItemBean(document, "tag-2", "content-2");
		document.addTraceableItem(traceableItem1);
		document.addTraceableItem(traceableItem2);
		
		TraceabilityMapBean traceabilityMap = new TraceabilityMapBean(document, traceableItem1, document, traceableItem2);
		traceableItem1.addDownstreamMap(traceabilityMap);
		traceableItem2.addUpstreamMap(traceabilityMap);
		document.addMapForAUpstreamItem(traceabilityMap);
		document.addMapForADownstreamItem(traceabilityMap);
	}
	
	@Test
	public void test() {
		showHtml( "- [tag-1] content-1\n"
				+ "- [tag-2]{tag-1} content-2\n"
				+ "  - nested text\n"
				+ "- this line is not a traceable text");
	}
	
	private void showHtml (String content) {
		
		DesignDocumentHtmlGenerator parser = new DesignDocumentHtmlGenerator(content, document);
		System.out.println("====================");
		System.out.println(content);
		System.out.println("--------------------");
		System.out.println(parser.getHtml());
		System.out.println("====================");
	}
}