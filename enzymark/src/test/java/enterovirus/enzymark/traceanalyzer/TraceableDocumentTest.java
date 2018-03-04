package enterovirus.enzymark.traceanalyzer;

import static org.junit.Assert.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class TraceableDocumentTest {

	@Test
	public void testGetTraceableItems() throws Exception {
		
		TraceableRepository repository = new TraceableRepository(new File("/fake/path/to/repository/root/directory"));
		
		String content = "normal text\n"
				+ "\n"
				+ "- [tag1] a traceable item.\n"
				+ "- [tag2]{tag1} a traceable item with in-document reference.";
		TraceableDocument document = new TraceableDocument(repository, "/fake/relative/file/path/for/document1", content);
		repository.addTraceableDocument(document);
		
		repository.refreshUpstreamAndDownstreamItems();
		
		List<String> tag1Downstream = new ArrayList<String>();
		for (TraceableItem item : document.getTraceableItems().get(0).getDownstreamItems()) {
			tag1Downstream.add(item.getTag());
		}
		
		List<String> tag1DownstreamExpect = new ArrayList<String>();
		tag1DownstreamExpect.add("tag2");
		
		assertEquals(tag1Downstream, tag1DownstreamExpect);
	}

}
