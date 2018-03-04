package enterovirus.enzymark.traceanalyzer;

import static org.junit.Assert.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import enterovirus.enzymark.traceanalyzer.TraceableDocument;
import enterovirus.enzymark.traceanalyzer.TraceableItem;
import enterovirus.enzymark.traceanalyzer.TraceableRepository;

public class TraceableRepositoryTest {
	
	/*
	 * Test with valid input
	 */
	@Rule public TemporaryFolder validRepositoryFolder = new TemporaryFolder();
	TraceableRepository validRepository;
	String validContent1;
	String validContent2;
	
	@Before
	public void initValidRepository() throws Exception {
		
		validContent1 = "normal text\n"
				+ "\n"
				+ "- [tag1] a traceable item.\n"
				+ "- [tag2]{tag1} a traceable item with in-document reference.";
		validContent2 = "normal text\n"
				+ "\n"
				+ "- [tag3]{tag1} a traceable item with cross-document reference.";
		
		File file1 = validRepositoryFolder.newFile("file1.md");
		FileUtils.writeStringToFile(file1, validContent1, "UTF-8");
		
		File file2 = validRepositoryFolder.newFile("file2.md");
		FileUtils.writeStringToFile(file2, validContent2, "UTF-8");
		
		validRepository = new TraceableRepository(validRepositoryFolder.getRoot());
	}
	
	@Test
	public void testValidRepositoryFromContent() throws Exception {
		
		TraceableDocument document1 = new TraceableDocument(validRepository, "/fake/relative/file/path/for/document1", validContent1);
		validRepository.addTraceableDocument(document1);
		
		TraceableDocument document2 = new TraceableDocument(validRepository, "/fake/relative/file/path/for/document2", validContent2);
		validRepository.addTraceableDocument(document2);
		
		validRepository.refreshUpstreamAndDownstreamItems();
		display(validRepository);
		
	}
	
	@Test
	public void testValidRepositoryFromFile() throws Exception {
		
		Collection<File> includeFiles = FileUtils.listFiles(validRepositoryFolder.getRoot(), new String[]{"md"}, true);
		
		for (File file : includeFiles) {
			TraceableDocument document = new TraceableDocument(validRepository, file);
			validRepository.addTraceableDocument(document);
		}
		
		validRepository.refreshUpstreamAndDownstreamItems();		
		display(validRepository);
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
	
	/*
	 * Test with invalid input: same file with non-unique tag
	 */
	@Rule public TemporaryFolder sameFileTagNotUniqueRepositoryFolder = new TemporaryFolder();
	TraceableRepository sameFileTagNotUniqueRepository;
	String sameFileTagNotUniqueContent;
	
	@Before
	public void initSameFileTagNotUniqueRepository() throws Exception {
		
		sameFileTagNotUniqueContent = "- [tag] a traceable item.\n"
				+ "- [tag] another traceable item with tag conflict.";
		
		File file = sameFileTagNotUniqueRepositoryFolder.newFile("file.md");
		FileUtils.writeStringToFile(file, sameFileTagNotUniqueContent, "UTF-8");
		
		sameFileTagNotUniqueRepository = new TraceableRepository(sameFileTagNotUniqueRepositoryFolder.getRoot());
	}
	
	@Test(expected = ItemTagNotUniqueException.class)
	public void testSameFileTagNotUniqueFromContent() throws Exception {
		
		TraceableDocument document = new TraceableDocument(sameFileTagNotUniqueRepository, "/fake/relative/file/path/for/document1", sameFileTagNotUniqueContent);
		sameFileTagNotUniqueRepository.addTraceableDocument(document);
		
		sameFileTagNotUniqueRepository.refreshUpstreamAndDownstreamItems();	
	}
	
	@Test(expected = ItemTagNotUniqueException.class)
	public void testSameFileTagNotUniqueFromFile() throws Exception {
		
		TraceableDocument document = new TraceableDocument(sameFileTagNotUniqueRepository, sameFileTagNotUniqueRepositoryFolder.getRoot().toPath().resolve("file.md").toFile());
		sameFileTagNotUniqueRepository.addTraceableDocument(document);
		
		sameFileTagNotUniqueRepository.refreshUpstreamAndDownstreamItems();	
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