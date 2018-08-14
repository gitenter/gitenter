package com.gitenter.enzymark.traceanalyzer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.Charsets;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;

import lombok.Getter;

public class TraceableDocument {

	private TraceableRepository repository;
	
	@Getter
	private String relativePath;
	
	@Getter
	private List<TraceableItem> traceableItems = new ArrayList<TraceableItem>();
	
	public TraceableDocument (TraceableRepository repository, String relativePath, String textContent) throws ItemTagNotUniqueException {
		
		this.repository = repository;
		this.relativePath = relativePath;
		
		parsing(repository, textContent);
	}
	
	public TraceableDocument(TraceableRepository repository, File documentFile) throws IOException, ItemTagNotUniqueException {
		
		this.repository = repository;
		this.relativePath = repository.getDirectory().toURI().relativize(documentFile.toURI()).getPath();
		
		List<String> lines = Files.readAllLines(documentFile.toPath(), Charsets.UTF_8);
		String textContent = String.join("\n", lines);
		
		parsing(repository, textContent);
	}
	
	private void parsing(TraceableRepository repository, String textContent)  throws ItemTagNotUniqueException {
		
		Parser parser = Parser.builder().build();
		Node node = parser.parse(textContent);
		TraceableItemVisitor visitor = new TraceableItemVisitor(this);
		node.accept(visitor);
		
		traceableItems = visitor.getTraceableItems();
		
		for (TraceableItem traceableItem : visitor.getTraceableItems()) {
			this.repository.putIntoTraceableItem(traceableItem);
		}
	}
}
