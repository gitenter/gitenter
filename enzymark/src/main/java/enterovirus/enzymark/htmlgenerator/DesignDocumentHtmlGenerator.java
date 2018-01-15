package enterovirus.enzymark.htmlgenerator;

import org.commonmark.node.*;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.NodeRenderer;
import org.commonmark.renderer.html.HtmlNodeRendererContext;
import org.commonmark.renderer.html.HtmlNodeRendererFactory;
import org.commonmark.renderer.html.HtmlRenderer;

import enterovirus.protease.domain.*;

public class DesignDocumentHtmlGenerator {

	private DocumentBean document;
	private String content;
	
	public DesignDocumentHtmlGenerator(DocumentBean document) {
		this.document = document;
		this.content = document.getContent();
	}
	
	public String getHtml() {
		
		Parser parser = Parser.builder().build();
		HtmlRenderer renderer = HtmlRenderer.builder()
		        .nodeRendererFactory(new HtmlNodeRendererFactory() {
		            public NodeRenderer create(HtmlNodeRendererContext context) {
		                return new TraceableItemNodeRenderer(context, document);
		            }
		        })
		        .nodeRendererFactory(new HtmlNodeRendererFactory() {
		            public NodeRenderer create(HtmlNodeRendererContext context) {
		                return new ImageNodeRenderer(context, document);
		            }
		        })
		        .build();
		
		Node node = parser.parse(content);
		String html = renderer.render(node);
		
		return html;
	}

}
