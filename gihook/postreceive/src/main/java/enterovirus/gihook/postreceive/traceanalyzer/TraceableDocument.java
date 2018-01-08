package enterovirus.gihook.postreceive.traceanalyzer;

import java.util.ArrayList;
import java.util.List;

import enterovirus.coatmark.plaintext.TraceableItemParser;

public class TraceableDocument {

	private TraceableRepository repository;
	private String relativeFilepath;
	private List<TraceableItem> traceableItems = new ArrayList<TraceableItem>();
	
	public TraceableDocument (TraceableRepository repository, String relativeFilepath, String textContent) {
		
		this.repository = repository;
		this.relativeFilepath = relativeFilepath;
		
		/*
		 * TODO:
		 * Split by "newline" which is compatible to Windows
		 * or Linux formats.
		 */
		int lineNumber = 1;
		for (String lineContent : textContent.split("\n")) {
			
			TraceableItemParser parsingResult = new TraceableItemParser(lineContent);
			
			if (parsingResult.isTraceableItem()) {
				TraceableItem item = new TraceableItem(lineNumber, parsingResult);
				traceableItems.add(item);
				this.repository.putIntoTraceableItem(item);
			}
			
			++lineNumber;
		}
	}
	
	public String getRelativeFilepath() {
		return relativeFilepath;
	}

	public List<TraceableItem> getTraceableItems() {
		return traceableItems;
	}
}
