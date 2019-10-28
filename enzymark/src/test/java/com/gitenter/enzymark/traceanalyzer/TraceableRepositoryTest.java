package com.gitenter.enzymark.traceanalyzer;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.gitenter.protease.domain.git.FileType;

public class TraceableRepositoryTest {
	
	@Test
	public void testAnalyzeUpstreamDownstreamOneDocument() throws Exception {
		
		TraceableRepository repository = new TraceableRepository();
		
		String textContent =
				  "- [tag1] a traceable item.\n"
				+ "- [tag2]{tag1} a traceable item with in-document reference.";
		TraceableFile document = new TraceableFile("/fake/relative/file/path/for/document1.md", FileType.MARKDOWN);
		document.parse(textContent);
		repository.addTraceableFile(document);
		
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
		String path1 = "/fake/relative/file/path/for/document1.md";
		TraceableFile document1 = new TraceableFile(path1, FileType.MARKDOWN);
		document1.parse(textContent1);
		repository.addTraceableFile(document1);
		
		String testContent2 = "- [tag3]{tag1,tag2} a traceable item with cross-document reference.";
		String path2 = "/fake/relative/file/path/for/document2.md";
		TraceableFile document2 = new TraceableFile(path2, FileType.MARKDOWN);
		document2.parse(testContent2);
		repository.addTraceableFile(document2);
		
		repository.refreshUpstreamAndDownstreamItems();
		
		assertEquals(repository.getTraceableFiles().size(), 2);
		
		for (TraceableFile document : repository.getTraceableFiles()) {
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
		TraceableFile document = new TraceableFile("/fake/relative/file/path/for/document.md", FileType.MARKDOWN);
		document.parse(textContent);
		repository.addTraceableFile(document);
		
		repository.refreshUpstreamAndDownstreamItems();	
	}
	
	@Test(expected = ItemTagNotUniqueException.class)
	public void testDifferentFilesTagNotUnique() throws Exception {
		
		TraceableRepository repository = new TraceableRepository();
		
		String content1 = "- [tag] a traceable item.";
		TraceableFile document1 = new TraceableFile("/fake/relative/file/path/for/document1.md", FileType.MARKDOWN);
		document1.parse(content1);
		repository.addTraceableFile(document1);
		
		String content2 = "- [tag] a traceable item with cross-document tag conflict.";
		TraceableFile document2 = new TraceableFile("/fake/relative/file/path/for/document2.md", FileType.MARKDOWN);
		document2.parse(content2);
		repository.addTraceableFile(document2);
		
		repository.refreshUpstreamAndDownstreamItems();	
	}
	
	@Test(expected = UpstreamTagNotExistException.class)
	public void testUpstreamTagNotExist() throws Exception {
		
		TraceableRepository repository = new TraceableRepository();
		
		String content = "- [tag]{refer-not-exist} another traceable item with reference not exist.";
		TraceableFile document = new TraceableFile("/fake/relative/file/path/for/documen.md", FileType.MARKDOWN);
		document.parse(content);
		repository.addTraceableFile(document);
		
		repository.refreshUpstreamAndDownstreamItems();	
	}
}