package com.gitenter.enzymark.traceanalyzer;

import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.List;

import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.junit.Test;

public class TraceableItemVisitorTest {
	
	@Test
	public void test() throws Exception {
		
		/*
		 * TODO:
		 * 
		 * For the case of mixed normal list and traceable item,
		 * think about a way how to handle that. (Should that be
		 * legal? Do we support it? How to support it?)
		 * 
		 */
		String content = "normal text\n"
				+ "\n"
				+ "- normal list item 1\n"
				+ "- normal list item 2\n"
				+ "\n"
				+ "- **bold** list item\n"
				+ "- *italic* list item\n"
				+ "\n"
				+ "more text\n"
				+ "\n"
				+ "- [tag]{refer} **bold** started traceable item.\n"
				+ "- [another-tag] another traceable item with **bold** and *italic*.\n"
				+ "- [the-third-tag]{} no refer";
		
		TraceableRepository repository = mock(TraceableRepository.class);
		TraceableDocument document = new TraceableDocument(repository, "/fake/relative/file/path", content);
		
		Parser parser = Parser.builder().build();
		Node node = parser.parse(content);
		TraceableItemVisitor visitor = new TraceableItemVisitor(document);
		node.accept(visitor);
	
		System.out.println(visitor.getTraceableItems().get(2).getContent());
		
		List<TraceableItem> resultItems = new ArrayList<TraceableItem>(); 
		resultItems.add(new TraceableItem("tag", "<strong>bold</strong> started traceable item.", document, new String[] {"refer"}));
		resultItems.add(new TraceableItem("another-tag", "another traceable item with <strong>bold</strong> and <em>italic</em>.", document, new String[] {} ));
//		assertEquals(visitor.getTraceableItems(), resultItems);
	}
}