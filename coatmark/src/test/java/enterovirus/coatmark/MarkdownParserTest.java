package enterovirus.coatmark;

import org.junit.Test;

public class MarkdownParserTest {

	@Test
	public void test() {

		MarkdownParser parser = new MarkdownParser("This is *Sparta*");
		System.out.println(parser.getHtml());
	}

}