package enterovirus.enzymark.traceanalyzer;

import static org.junit.Assert.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import enterovirus.enzymark.traceanalyzer.TraceableDocument;
import enterovirus.enzymark.traceanalyzer.TraceableItem;
import enterovirus.enzymark.traceanalyzer.TraceableRepository;

public class TraceableRepositoryTest {
	
	@Test
	public void test1() throws Exception {
		
		TraceableRepository repository = new TraceableRepository(new File("/fake/path/to/repository/root/directory"));
		
		String content1 = "normal text\n"
				+ "\n"
				+ "- [tag1] a traceable item.\n"
				+ "- [tag2]{tag1} a traceable item with in-document reference.";
		TraceableDocument document1 = new TraceableDocument(repository, "/fake/relative/file/path/for/document1", content1);
		repository.addTraceableDocument(document1);
		
		String content2 = "normal text\n"
				+ "\n"
				+ "- [tag3]{tag1} a traceable item with cross-document reference.";
		TraceableDocument document2 = new TraceableDocument(repository, "/fake/relative/file/path/for/document2", content2);
		repository.addTraceableDocument(document2);
		
		repository.refreshUpstreamAndDownstreamItems();
		
		List<String> tag1Downstream = new ArrayList<String>();
		for (TraceableItem item : document1.getTraceableItems().get(0).getDownstreamItems()) {
			tag1Downstream.add(item.getTag());
		}
		
		List<String> tag1DownstreamExpect = new ArrayList<String>();
		tag1DownstreamExpect.add("tag2");
		tag1DownstreamExpect.add("tag3");
		
		assertEquals(tag1Downstream, tag1DownstreamExpect);
		
		display(repository);
	}
	
	private void display (TraceableRepository repository) {

		for (TraceableDocument document : repository.getTraceableDocuments()) {
			System.out.println(document.getRelativeFilepath());
			for (TraceableItem item : document.getTraceableItems()) {
				System.out.println("\t"+item.getTag());
				
				System.out.print("\t\tupstream: ");
				for (TraceableItem upstreamItem : item.getUpstreamItems()) {
					System.out.print(upstreamItem.getTag()+",");
				}
				System.out.println("");
				
				System.out.print("\t\tdownstream: ");
				for (TraceableItem downstreamItem : item.getDownstreamItems()) {
					System.out.print(downstreamItem.getTag()+",");
				}
				System.out.println("");
			}
		}
	}
	
	@Test(expected = ItemTagNotUniqueException.class)
	public void test2() throws Exception {
		
		TraceableRepository repository = new TraceableRepository(new File("/fake/path/to/repository/root/directory"));
		
		String content = "- [tag] a traceable item.\n"
				+ "- [tag] another traceable item with tag conflict.";
		TraceableDocument document = new TraceableDocument(repository, "/fake/relative/file/path/for/document1", content);
		repository.addTraceableDocument(document);
		
		repository.refreshUpstreamAndDownstreamItems();	
	}
	
	@Test(expected = ItemTagNotUniqueException.class)
	public void test3() throws Exception {
		
		TraceableRepository repository = new TraceableRepository(new File("/fake/path/to/repository/root/directory"));
		
		String content1 = "- [tag] a traceable item.";
		TraceableDocument document1 = new TraceableDocument(repository, "/fake/relative/file/path/for/document1", content1);
		repository.addTraceableDocument(document1);
		
		String content2 = "- [tag] a traceable item with cross-document tag conflict.";
		TraceableDocument document2 = new TraceableDocument(repository, "/fake/relative/file/path/for/document2", content2);
		repository.addTraceableDocument(document2);
		
		repository.refreshUpstreamAndDownstreamItems();	
	}
	
	@Test(expected = UpstreamTagNotExistException.class)
	public void test4() throws Exception {
		
		TraceableRepository repository = new TraceableRepository(new File("/fake/path/to/repository/root/directory"));
		
		String content = "- [tag]{refer-not-exist} another traceable item with reference not exist.";
		TraceableDocument document = new TraceableDocument(repository, "/fake/relative/file/path/for/document1", content);
		repository.addTraceableDocument(document);
		
		repository.refreshUpstreamAndDownstreamItems();	
	}
}