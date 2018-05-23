package enterovirus.enzymark.htmlgenerator;

import java.io.File;

import org.junit.Test;

public class DefaultHtmlGeneratorTest {
	
	@Test
	public void test() throws Exception {
		File markdownFile = new File(System.getProperty("user.home"), "Workspace/enterovirus/capsid/src/main/resources/markdown/about.md");
		HtmlGenerator htmlGenerator = new DefaultHtmlGenerator(markdownFile);
		System.out.println(htmlGenerator.getHtml());
	}
}