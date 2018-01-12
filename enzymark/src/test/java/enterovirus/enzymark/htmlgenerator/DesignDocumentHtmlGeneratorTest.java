package enterovirus.enzymark.htmlgenerator;

import org.junit.Test;

import enterovirus.enzymark.htmlgenerator.DesignDocumentHtmlGenerator;
import enterovirus.protease.domain.*;

public class DesignDocumentHtmlGeneratorTest {
	
	@Test
	public void test() {
		
		CommitValidBean commit = new CommitValidBean();
		String relativeFilepath = "fake-path-for-a-document.md";
		DocumentBean document = new DocumentBean(commit, relativeFilepath);
		
		String content = 
				  "- [tag-1] content-1\n"
				+ "- [tag-2]{tag-1} content-2\n"
				+ "  - nested text\n"
				+ "- this line is not a traceable text";
		document.setContent(content);
		
		TraceableItemBean traceableItem1 = new TraceableItemBean(document, "tag-1", "content-1");
		TraceableItemBean traceableItem2 = new TraceableItemBean(document, "tag-2", "content-2");
		document.addTraceableItem(traceableItem1);
		document.addTraceableItem(traceableItem2);
		
		TraceabilityMapBean traceabilityMap = new TraceabilityMapBean(traceableItem1, traceableItem2);
		traceableItem1.addDownstreamMap(traceabilityMap);
		traceableItem2.addUpstreamMap(traceabilityMap);

		showHtml(document);
	}
	
	private void showHtml (DocumentBean document) {
		
		DesignDocumentHtmlGenerator parser = new DesignDocumentHtmlGenerator(document);
		System.out.println("====================");
		System.out.println(document.getContent());
		System.out.println("--------------------");
		System.out.println(parser.getHtml());
		System.out.println("====================");
	}
}