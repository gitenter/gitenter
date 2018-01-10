package enterovirus.enzymark.traceanalyzer;

import java.util.ArrayList;
import java.util.List;

import org.commonmark.node.Node;
import org.commonmark.parser.Parser;

public class TraceableDocument {

	private TraceableRepository repository;
	private String relativeFilepath;
	private List<TraceableItem> traceableItems = new ArrayList<TraceableItem>();
	
	public TraceableDocument (TraceableRepository repository, String relativeFilepath, String textContent) throws ItemTagNotUniqueException {
		
		this.repository = repository;
		this.relativeFilepath = relativeFilepath;
		
		Parser parser = Parser.builder().build();
		Node node = parser.parse(textContent);
		TraceableItemVisitor visitor = new TraceableItemVisitor(this);
		node.accept(visitor);
		
		traceableItems = visitor.getTraceableItems();
		
		for (TraceableItem traceableItem : visitor.getTraceableItems()) {
			this.repository.putIntoTraceableItem(traceableItem);
		}
	}
	
	public String getRelativeFilepath() {
		return relativeFilepath;
	}

	public List<TraceableItem> getTraceableItems() {
		return traceableItems;
	}
}
