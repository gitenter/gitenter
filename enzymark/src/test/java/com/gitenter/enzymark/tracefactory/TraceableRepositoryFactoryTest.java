package com.gitenter.enzymark.tracefactory;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.gitenter.enzymark.traceanalyzer.TraceAnalyzerException;
import com.gitenter.enzymark.traceanalyzer.TraceableFile;
import com.gitenter.enzymark.traceanalyzer.TraceableItem;
import com.gitenter.enzymark.traceanalyzer.TraceableRepository;
import com.gitenter.gitar.GitFolder;
import com.gitenter.gitar.GitNormalRepository;
import com.gitenter.gitar.GitWorkspace;

public class TraceableRepositoryFactoryTest {
	
	@TempDir
	public File folder;
	
	@Test 
	public void testMarkdownCanBeTraceable(@TempDir File directory) throws Exception {
		
		GitNormalRepository repository = GitNormalRepository.getInstance(directory);
		GitWorkspace workspace = repository.getCurrentBranch().checkoutTo();
		
		addAFile(directory, "file.md", "");
		
		workspace.add();
		workspace.commit("dummy commit message");
		GitFolder gitFolder = workspace.getRoot();
		
		TraceableRepositoryFactory factory = new TraceableRepositoryFactory();
		TraceableRepository traceableRepository = factory.getTraceableRepository(gitFolder);
		
		assertEquals(traceableRepository.getTraceableFiles().size(), 1);
	}
	
	@Test 
	public void testConfigYamlCannotBeTraceable(@TempDir File directory) throws Exception {

		GitNormalRepository repository = GitNormalRepository.getInstance(directory);
		GitWorkspace workspace = repository.getCurrentBranch().checkoutTo();
		
		addAFile(directory, "gitenter.yml", "");
		
		workspace.add();
		workspace.commit("dummy commit message");
		GitFolder gitFolder = workspace.getRoot();
		
		TraceableRepositoryFactory factory = new TraceableRepositoryFactory();
		TraceableRepository traceableRepository = factory.getTraceableRepository(gitFolder);
		
		assertEquals(traceableRepository.getTraceableFiles().size(), 0);	
	}

	@Test
	public void testBuildTraceableRepositorySingleFile(@TempDir File directory) throws IOException, GitAPIException, TraceAnalyzerException {
		
		String textContent = 
				  "- [tag1] a traceable item.\n"
				+ "- [tag2]{tag1} a traceable item with in-document reference.";

		GitNormalRepository repository = GitNormalRepository.getInstance(directory);
		GitWorkspace workspace = repository.getCurrentBranch().checkoutTo();
		
		addAFile(directory, "file.md", textContent);
		
		workspace.add();
		workspace.commit("dummy commit message");
		GitFolder gitFolder = workspace.getRoot();
		
		TraceableRepositoryFactory factory = new TraceableRepositoryFactory();
		TraceableRepository traceableRepository = factory.getTraceableRepository(gitFolder);
		
		assertEquals(traceableRepository.getTraceableFiles().size(), 1);
		TraceableFile document = traceableRepository.getTraceableFiles().get(0);
		
		for (TraceableItem currentItem : document.getTraceableItems()) {

			if (currentItem.getTag().equals("tag1")) {
				assertEquals(currentItem.getUpstreamItems().size(), 0);
				assertEquals(currentItem.getDownstreamItems().size(), 1);
			}
			
			if (currentItem.getTag().equals("tag2")) {
				assertEquals(currentItem.getUpstreamItems().size(), 1);
				assertEquals(currentItem.getDownstreamItems().size(), 0);
			}
		}
	}
	
	@Test
	public void testBuildTraceableRepositoryHierarchyOfFiles(@TempDir File directory) throws IOException, GitAPIException, TraceAnalyzerException {
		
		String textContent1 = 
				  "- [tag1] a traceable item.\n"
				+ "- [tag2]{tag1} a traceable item with in-document reference.";
		
		String textContent2 = "- [tag3]{tag1,tag2} a traceable item with cross-document reference.";

		GitNormalRepository repository = GitNormalRepository.getInstance(directory);
		GitWorkspace workspace = repository.getCurrentBranch().checkoutTo();
		
		addAFile(directory, "root-file.md", textContent1);
		
		File subfolder = new File(directory, "nested-folder");
		subfolder.mkdir();
		addAFile(subfolder, "nested-file.md", textContent2);
		
		workspace.add();
		workspace.commit("dummy commit message");
		GitFolder gitFolder = workspace.getRoot();
		
		TraceableRepositoryFactory factory = new TraceableRepositoryFactory();
		TraceableRepository traceableRepository = factory.getTraceableRepository(gitFolder);
		
		assertEquals(traceableRepository.getTraceableFiles().size(), 2);
		
		for (TraceableFile document : traceableRepository.getTraceableFiles()) {
			for (TraceableItem currentItem : document.getTraceableItems()) {
	
				if (currentItem.getTag().equals("tag1")) {
					assertEquals(currentItem.getUpstreamItems().size(), 0);
					assertEquals(currentItem.getDownstreamItems().size(), 2);
				}
				
				if (currentItem.getTag().equals("tag2")) {
					assertEquals(currentItem.getUpstreamItems().size(), 1);
					assertEquals(currentItem.getUpstreamItems().get(0).getTag(), "tag1");
					assertEquals(currentItem.getDownstreamItems().get(0).getTag(), "tag3");
				}
				
				if (currentItem.getTag().equals("tag3")) {
					assertEquals(currentItem.getUpstreamItems().size(), 2);
					assertEquals(currentItem.getDownstreamItems().size(), 0);
				}
			}
		}
	}
	
	private void addAFile(File directory, String filename, String textContent) throws IOException {
		
		File file = new File(directory, filename);
		file.createNewFile();
		FileWriter writer = new FileWriter(file);
		writer.write(textContent);
		writer.close();
	}
}
