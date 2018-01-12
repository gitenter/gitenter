package enterovirus.enzymark.htmlgenerator;

import org.commonmark.node.*;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.NodeRenderer;
import org.commonmark.renderer.html.HtmlNodeRendererContext;
import org.commonmark.renderer.html.HtmlNodeRendererFactory;
import org.commonmark.renderer.html.HtmlRenderer;

import enterovirus.protease.domain.*;

public class DesignDocumentHtmlGenerator {

	private String content;
	private DocumentBean documentBean;
	
	public DesignDocumentHtmlGenerator(String content, DocumentBean documentBean) {
		this.content = content;
		this.documentBean = documentBean;
	}
	
	public String getHtml() {
		
		Parser parser = Parser.builder().build();
		HtmlRenderer renderer = HtmlRenderer.builder()
		        .nodeRendererFactory(new HtmlNodeRendererFactory() {
		            public NodeRenderer create(HtmlNodeRendererContext context) {
		                return new TraceableItemNodeRenderer(context, documentBean);
		            }
		        })
		        .build();
		
		Node node = parser.parse(content);
		String html = renderer.render(node);
		
		return html;
	}

}
