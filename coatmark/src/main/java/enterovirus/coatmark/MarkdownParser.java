package enterovirus.coatmark;

/*
 * TODO:
 * Seriously decide what packet to use. "flexmark-java" seems the one
 * most people recommend nowadays. It is a fork of atlassian's
 * "commonmark-java" so maybe the later one is a safer choice.
 * 
 * No matter what, "pegdown" (the still most popular one) is out of
 * date so we shouldn't use that one.
 */
import org.commonmark.node.*;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

public class MarkdownParser {

	private String markdownContent;
	
	public MarkdownParser(String markdownContent) {
		this.markdownContent = markdownContent;
	}
	
	public String getHtml() {
		
		Parser parser = Parser.builder().build();
		Node document = parser.parse(markdownContent);
		HtmlRenderer renderer = HtmlRenderer.builder().build();
		String html = renderer.render(document);
		
		return html;
	}

}
