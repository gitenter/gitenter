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
				  "- [tag-1] **Bold started** content of tag-1\n"
				+ "- [tag-2]{tag-1} content of tag-2 with **bold** and *italic*.\n"
				+ "  - nested text 1\n"
				+ "  - nested text 2 with **bold** and *italic*.\n"
				+ "- this line is not a traceable text\n"
				+ "- **bold** started list item\n"
				+ "- *italic* started list item\n"
				+ "- paragraph with **bold** and *italic*.\n"
				+ "\n"
				+ "![alt text](sample.png \"title text\")\n";
		document.setContent(content);
		
		TraceableItemBean traceableItem1 = new TraceableItemBean(document, "tag-1", "content-1");
		TraceableItemBean traceableItem2 = new TraceableItemBean(document, "tag-2", "content-2");
		document.addTraceableItem(traceableItem1);
		document.addTraceableItem(traceableItem2);
		
		traceableItem1.addDownstreamItem(traceableItem2);
		traceableItem2.addUpstreamItem(traceableItem1);

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