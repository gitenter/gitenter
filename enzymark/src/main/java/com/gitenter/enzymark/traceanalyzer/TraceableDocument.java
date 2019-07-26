package com.gitenter.enzymark.traceanalyzer;

import java.util.ArrayList;
import java.util.List;

import org.commonmark.node.Node;
import org.commonmark.parser.Parser;

import lombok.Getter;

public class TraceableDocument {
	
	@Getter
	private String relativePath;
	
	@Getter
	private List<TraceableItem> traceableItems = new ArrayList<TraceableItem>();
	
	public TraceableDocument(String relativePath) throws ItemTagNotUniqueException {
		this.relativePath = relativePath;
	}
	
//	public TraceableDocument(TraceableRepository repository, File documentFile) throws IOException, ItemTagNotUniqueException {
//		
//		this.repository = repository;
//		this.relativePath = repository.getDirectory().toURI().relativize(documentFile.toURI()).getPath();
//		
//		List<String> lines = Files.readAllLines(documentFile.toPath(), Charsets.UTF_8);
//		String textContent = String.join("\n", lines);
//		
////		parsing(repository, textContent);
//	}
	
	void addTraceableItem(TraceableItem item) {
		traceableItems.add(item);
	}
	
	public void parse(String textContent) {
		
		Parser parser = Parser.builder().build();
		Node node = parser.parse(textContent);
		TraceableItemVisitor visitor = new TraceableItemVisitor(this);
		node.accept(visitor);
	}
}
