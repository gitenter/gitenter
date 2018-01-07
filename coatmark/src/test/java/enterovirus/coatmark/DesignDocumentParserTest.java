package enterovirus.coatmark;

import org.junit.Test;

public class DesignDocumentParserTest {

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
		showHtml("- item1\n  - item1 subitem1\n- item2");
	}

	@Test
	public void test4() {
		showHtml("Example:\n\n    code");
	}
	
	private void showHtml (String content) {
		
		DesignDocumentParser parser = new DesignDocumentParser(content);
		System.out.println("====================");
		System.out.println(content);
		System.out.println("--------------------");
		System.out.println(parser.getHtml());
		System.out.println("====================");
	}
}