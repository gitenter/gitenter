package enterovirus.gihook.postreceive.traceanalyzer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.Test;

public class TraceableRepositoryTest {

	@Test
	public void test() throws IOException {
		
		String[] relativeFilepaths = {"document-1.md", "document-2.md"};
		
		File repositoryDirectory = new File("/home/beta/Workspace/enterovirus-test/one-commit-traceability/org/repo/");
		
		TraceableRepository repository = new TraceableRepository(repositoryDirectory);
		
		for (String relativeFilepath : relativeFilepaths) {
			File filepath = new File(repositoryDirectory, relativeFilepath);
			String textContent = readFromFile(filepath);
			TraceableDocument document = new TraceableDocument(repository, relativeFilepath, textContent);
			repository.addTraceableDocument(document);
		}
		
		repository.refreshUpstreamAndDownstreamItems();
		
		display(repository);
	}
	
	private String readFromFile (File filepath) throws IOException {
		return new String(Files.readAllBytes(Paths.get(filepath.getAbsolutePath())));
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
}