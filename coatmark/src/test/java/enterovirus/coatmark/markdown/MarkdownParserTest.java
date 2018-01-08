package enterovirus.coatmark.markdown;

import org.junit.Test;

import enterovirus.coatmark.markdown.MarkdownParser;

public class MarkdownParserTest {

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
		showHtml("- item1\n- item2");
	}
	
	private void showHtml (String content) {
		
		MarkdownParser parser = new MarkdownParser(content);
		System.out.println("====================");
		System.out.println(content);
		System.out.println("--------------------");
		System.out.println(parser.getHtml());
		System.out.println("====================");
	}
}