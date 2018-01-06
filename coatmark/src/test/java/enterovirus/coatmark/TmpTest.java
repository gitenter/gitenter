package enterovirus.coatmark;

import org.junit.Test;

import org.commonmark.node.*;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

public class TmpTest {

	@Test
	public void test() {

		Parser parser = Parser.builder().build();
		Node document = parser.parse("This is *Sparta*");
		HtmlRenderer renderer = HtmlRenderer.builder().build();
		String html = renderer.render(document);
		System.out.println(html);  // "<p>This is <em>Sparta</em></p>\n"
	}

}