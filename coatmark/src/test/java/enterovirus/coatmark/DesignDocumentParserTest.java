package enterovirus.coatmark;

import org.junit.Before;
import org.junit.Test;

import enterovirus.protease.domain.*;

public class DesignDocumentParserTest {
	
	private DocumentModifiedBean document;

	@Before
	public void init() {
		CommitBean commit = new CommitBean();
		String relativeFilepath = "/a-document.md";
		document = new DocumentModifiedBean(commit, relativeFilepath);
		
//		TraceableItemBean item1 = TraceableItemBean(document, )
	}
	
	@Test
	public void test1() {
		showHtml("This is *Sparta*");
	}

	@Test
	public void test2() {
		showHtml("Example:\n\n    code");
	}
	
	@Test
	public void test3() {
		showHtml( "- [tag-1] content-1\n"
				+ "- [tag-2]{tag-1} content-2\n"
				+ "  - nested text\n"
				+ "- not a traceable text");
	}

	@Test
	public void test4() {
		showHtml("Example:\n\n    code");
	}
	
	private void showHtml (String content) {
		
		DesignDocumentParser parser = new DesignDocumentParser(content, document);
		System.out.println("====================");
		System.out.println(content);
		System.out.println("--------------------");
		System.out.println(parser.getHtml());
		System.out.println("====================");
	}
}