package enterovirus.enzymark.traceanalyzer;

import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.junit.Test;

import enterovirus.enzymark.traceanalyzer.TraceableItem;
import enterovirus.enzymark.traceanalyzer.TraceableItemVisitor;

public class TraceableItemVisitorTest {
	
	@Test
	public void test() {
		
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
				+ "more text\n"
				+ "\n"
				+ "- [tag]{refer} traceable item\n"
				+ "- [another-tag] another traceable item\n";
		
		Parser parser = Parser.builder().build();
		Node node = parser.parse(content);
		TraceableItemVisitor visitor = new TraceableItemVisitor();
		node.accept(visitor);
		
		for (TraceableItem traceableItem : visitor.getTraceableItems()) {
			System.out.println(traceableItem.getTag());
		}
	}
}