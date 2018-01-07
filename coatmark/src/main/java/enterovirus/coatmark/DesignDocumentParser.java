package enterovirus.coatmark;

import org.commonmark.node.*;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.NodeRenderer;
import org.commonmark.renderer.html.HtmlNodeRendererContext;
import org.commonmark.renderer.html.HtmlNodeRendererFactory;
import org.commonmark.renderer.html.HtmlRenderer;

public class DesignDocumentParser {

	private String content;
	
	public DesignDocumentParser(String content) {
		this.content = content;
	}
	
	public String getHtml() {
		
		Parser parser = Parser.builder().build();
		HtmlRenderer renderer = HtmlRenderer.builder()
		        .nodeRendererFactory(new HtmlNodeRendererFactory() {
		            public NodeRenderer create(HtmlNodeRendererContext context) {
		                return new TraceableItemNodeRenderer(context);
		            }
		        })
		        .build();
		
		Node document = parser.parse(content);
		String html = renderer.render(document);
		
		return html;
	}

}
