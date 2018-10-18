package com.gitenter.enzymark.traceanalyzer;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class TraceableRepositoryTest {
	
	@Test
	public void testAnalyzeUpstreamDownstreamOneDocument() throws Exception {
		
		TraceableRepository repository = new TraceableRepository();
		
		String textContent =
				  "- [tag1] a traceable item.\n"
				+ "- [tag2]{tag1} a traceable item with in-document reference.";
		TraceableDocument document = new TraceableDocument("/fake/relative/file/path/for/document1");
		document.parse(textContent);
		repository.addTraceableDocument(document);
		
		repository.refreshUpstreamAndDownstreamItems();		
		
		for (TraceableItem masterItem : document.getTraceableItems()) {
			
			if (masterItem.getTag().equals("tag1")) {
				
				assertEquals(masterItem.getUpstreamItems().size(), 0);
				
				List<String> downstream = new ArrayList<String>();
				for (TraceableItem referItem : masterItem.getDownstreamItems()) {
					downstream.add(referItem.getTag());
				}
				
				List<String> downstreamExpected = new ArrayList<String>();
				downstreamExpected.add("tag2");
				assertEquals(downstream, downstreamExpected);
			}
			
			if (masterItem.getTag().equals("tag2")) {
				
				assertEquals(masterItem.getDownstreamItems().size(), 0);
				
				List<String> upstream = new ArrayList<String>();
				for (TraceableItem referItem : masterItem.getUpstreamItems()) {
					upstream.add(referItem.getTag());
				}
				
				List<String> upstreamExpected = new ArrayList<String>();
				upstreamExpected.add("tag1");
				assertEquals(upstream, upstreamExpected);
			}
		}
	}
	
	@Test
	public void testAnalyzeUpstreamDownstreamMultipleDocuments() throws Exception {
		
		TraceableRepository repository = new TraceableRepository();
		
		String textContent1 = 
				  "- [tag1] a traceable item.\n"
				+ "- [tag2]{tag1} a traceable item with in-document reference.";
		String path1 = "/fake/relative/file/path/for/document1";
		TraceableDocument document1 = new TraceableDocument(path1);
		document1.parse(textContent1);
		repository.addTraceableDocument(document1);
		
		String testContent2 = "- [tag3]{tag1,tag2} a traceable item with cross-document reference.";
		String path2 = "/fake/relative/file/path/for/document2";
		TraceableDocument document2 = new TraceableDocument(path2);
		document2.parse(testContent2);
		repository.addTraceableDocument(document2);
		
		repository.refreshUpstreamAndDownstreamItems();
		
		assertEquals(repository.getTraceableDocuments().size(), 2);
		
		for (TraceableDocument document : repository.getTraceableDocuments()) {
			for (TraceableItem currentItem : document.getTraceableItems()) {
				
				if (currentItem.getTag().equals("tag1")) {
					
					assertEquals(document.getRelativePath(), path1);
					
					assertEquals(currentItem.getUpstreamItems().size(), 0);
					
					List<String> downstream = new ArrayList<String>();
					for (TraceableItem referItem : currentItem.getDownstreamItems()) {
						downstream.add(referItem.getTag());
					}
					
					List<String> downstreamExpected = new ArrayList<String>();
					downstreamExpected.add("tag2");
					downstreamExpected.add("tag3");
					assertEquals(downstream, downstreamExpected);
				}
				
				if (currentItem.getTag().equals("tag2")) {
					
					assertEquals(document.getRelativePath(), path1);
					
					List<String> upstream = new ArrayList<String>();
					for (TraceableItem referItem : currentItem.getUpstreamItems()) {
						upstream.add(referItem.getTag());
					}
					
					List<String> upstreamExpected = new ArrayList<String>();
					upstreamExpected.add("tag1");
					assertEquals(upstream, upstreamExpected);
					
					List<String> downstream = new ArrayList<String>();
					for (TraceableItem referItem : currentItem.getDownstreamItems()) {
						downstream.add(referItem.getTag());
					}
					
					List<String> downstreamExpected = new ArrayList<String>();
					downstreamExpected.add("tag3");
					assertEquals(downstream, downstreamExpected);
				}
				
				if (currentItem.getTag().equals("tag3")) {
					
					assertEquals(document.getRelativePath(), path2);
					
					assertEquals(currentItem.getDownstreamItems().size(), 0);
					
					List<String> upstream = new ArrayList<String>();
					for (TraceableItem referItem : currentItem.getUpstreamItems()) {
						upstream.add(referItem.getTag());
					}
					
					List<String> upstreamExpected = new ArrayList<String>();
					upstreamExpected.add("tag1");
					upstreamExpected.add("tag2");
					assertEquals(upstream, upstreamExpected);
				}
			}
		}
	}
	
	@Test(expected = ItemTagNotUniqueException.class)
	public void testSameFileTagNotUnique() throws Exception {
		
		TraceableRepository repository = new TraceableRepository();
		
		String textContent = 
				  "- [tag] a traceable item.\n"
				+ "- [tag] another traceable item with tag conflict.";
		TraceableDocument document = new TraceableDocument("/fake/relative/file/path/for/document1");
		document.parse(textContent);
		repository.addTraceableDocument(document);
		
		repository.refreshUpstreamAndDownstreamItems();	
	}
	
	@Test(expected = ItemTagNotUniqueException.class)
	public void testDifferentFilesTagNotUnique() throws Exception {
		
		TraceableRepository repository = new TraceableRepository();
		
		String content1 = "- [tag] a traceable item.";
		TraceableDocument document1 = new TraceableDocument("/fake/relative/file/path/for/document1");
		document1.parse(content1);
		repository.addTraceableDocument(document1);
		
		String content2 = "- [tag] a traceable item with cross-document tag conflict.";
		TraceableDocument document2 = new TraceableDocument("/fake/relative/file/path/for/document2");
		document2.parse(content2);
		repository.addTraceableDocument(document2);
		
		repository.refreshUpstreamAndDownstreamItems();	
	}
	
	@Test(expected = UpstreamTagNotExistException.class)
	public void testUpstreamTagNotExist() throws Exception {
		
		TraceableRepository repository = new TraceableRepository();
		
		String content = "- [tag]{refer-not-exist} another traceable item with reference not exist.";
		TraceableDocument document = new TraceableDocument("/fake/relative/file/path/for/document1");
		document.parse(content);
		repository.addTraceableDocument(document);
		
		repository.refreshUpstreamAndDownstreamItems();	
	}
}