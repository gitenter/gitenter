package com.gitenter.enzymark.htmlgenerator;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.junit.Test;
import org.mockito.Mockito;

import com.gitenter.protease.domain.git.DocumentBean;
import com.gitenter.protease.domain.git.FileType;
import com.gitenter.protease.domain.traceability.TraceableDocumentBean;
import com.gitenter.protease.domain.traceability.TraceableItemBean;

public class DesignDocumentHtmlGeneratorTest {
	
	@Test
	public void testMarkdownBold() throws IOException, GitAPIException {
		
		DocumentBean document = new DocumentBean();
		document.setFileType(FileType.MARKDOWN);
		
		String content = "**Bold**\n";
		String expectedOutput = "<p><strong>Bold</strong></p>\n";
		DocumentBean spyDocument = Mockito.spy(document);
		Mockito.doReturn(content).when(spyDocument).getContent();
		
		DesignDocumentHtmlGenerator parser = new DesignDocumentHtmlGenerator(spyDocument);
		assertEquals(parser.getHtml(), expectedOutput);
	}

	@Test
	public void testMarkdownItalic() throws IOException, GitAPIException {
		
		DocumentBean document = new DocumentBean();
		document.setFileType(FileType.MARKDOWN);
		
		String content = "*italic*\n";
		String expectedOutput = "<p><em>italic</em></p>\n";
		DocumentBean spyDocument = Mockito.spy(document);
		Mockito.doReturn(content).when(spyDocument).getContent();
		
		DesignDocumentHtmlGenerator parser = new DesignDocumentHtmlGenerator(spyDocument);
		assertEquals(parser.getHtml(), expectedOutput);
	}
	
	@Test
	public void testMarkdownNormalBubbleItem() throws IOException, GitAPIException {
		
		DocumentBean document = new DocumentBean();
		document.setFileType(FileType.MARKDOWN);
		
		String content = "- This line is not a traceable text.\n";
		String expectedOutput = "<ul>\n"
				+ "<li>This line is not a traceable text.</li>\n"
				+ "</ul>\n";
		DocumentBean spyDocument = Mockito.spy(document);
		Mockito.doReturn(content).when(spyDocument).getContent();
		
		DesignDocumentHtmlGenerator parser = new DesignDocumentHtmlGenerator(spyDocument);
		assertEquals(parser.getHtml(), expectedOutput);
	}
	
	@Test
	public void testMarkdownTraceableItem() throws IOException, GitAPIException {
		
		DocumentBean document = new DocumentBean();
		document.setRelativePath("fake-path-for-a-document.md");
		document.setFileType(FileType.MARKDOWN);
		
		TraceableDocumentBean traceableDocument = new TraceableDocumentBean();
		traceableDocument.setDocument(document);
		document.setTraceableDocument(traceableDocument);
		
		TraceableItemBean traceableItem1 = new TraceableItemBean();
		traceableDocument.addTraceableItem(traceableItem1);
		traceableItem1.setTraceableDocument(traceableDocument);
		traceableItem1.setItemTag("tag-1");
		traceableItem1.setContent("content-1");
		
		TraceableItemBean traceableItem2 = new TraceableItemBean();
		traceableDocument.addTraceableItem(traceableItem2);
		traceableItem2.setTraceableDocument(traceableDocument);
		traceableItem2.setItemTag("tag-2");
		traceableItem2.setContent("content-2");
		
		String content = 
				  "- [tag-1] content-1.\n"
				+ "- [tag-2]{tag-1} content-2.\n";
		String expectedOutput = "<ul>\n"
				+ "<li id=\"tag-1\" class=\"traceable-item\"><form method=\"GET\" action=\"#tag-1\"><input class=\"original\" type=\"submit\" value=\"tag-1\"></form>content-1.</li>\n"
				+ "<li id=\"tag-2\" class=\"traceable-item\"><form method=\"GET\" action=\"#tag-2\"><input class=\"original\" type=\"submit\" value=\"tag-2\"></form>content-2.</li>\n" 
				+ "</ul>\n";
		DocumentBean spyDocument = Mockito.spy(document);
		Mockito.doReturn(content).when(spyDocument).getContent();
		
		DesignDocumentHtmlGenerator parser = new DesignDocumentHtmlGenerator(spyDocument);
		assertEquals(parser.getHtml(), expectedOutput);
	}
	
	@Test
	public void testMarkdownNestedBubbleItem() throws IOException, GitAPIException {
		
		DocumentBean document = new DocumentBean();
		document.setFileType(FileType.MARKDOWN);
		
		String content = "- Outer layer.\n" +
				"  - Inner layer.\n";
		String expectedOutput = "<ul>\n" + 
				"<li>Outer layer.\n" + 
				"<ul>\n" + 
				"<li>Inner layer.</li>\n" + 
				"</ul>\n" + 
				"</li>\n" +
				"</ul>\n";
		DocumentBean spyDocument = Mockito.spy(document);
		Mockito.doReturn(content).when(spyDocument).getContent();
		
		DesignDocumentHtmlGenerator parser = new DesignDocumentHtmlGenerator(spyDocument);
		assertEquals(parser.getHtml(), expectedOutput);
	}
	
	@Test
	public void testMarkdownImages() throws IOException, GitAPIException {
		
		DocumentBean document = new DocumentBean();
		document.setRelativePath("fake-path-for-a-document.md");
		document.setFileType(FileType.MARKDOWN);
		
		String content = "![alt text](sample.png \"title text\")\n";
		String expectedOutput = "<p><img src=\"../../blobs/directories/sample.png\" alt=\"alt text\" title=\"title text\" /></p>\n";
		DocumentBean spyDocument = Mockito.spy(document);
		Mockito.doReturn(content).when(spyDocument).getContent();
		
		DesignDocumentHtmlGenerator parser = new DesignDocumentHtmlGenerator(spyDocument);
		assertEquals(parser.getHtml(), expectedOutput);
	}
}