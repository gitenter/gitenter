package enterovirus.coatmark;

import org.commonmark.node.*;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.NodeRenderer;
import org.commonmark.renderer.html.HtmlNodeRendererContext;
import org.commonmark.renderer.html.HtmlNodeRendererFactory;
import org.commonmark.renderer.html.HtmlRenderer;

import enterovirus.protease.domain.DocumentModifiedBean;

public class DesignDocumentParser {

	private String content;
	private DocumentModifiedBean document;
	
	public DesignDocumentParser(String content, DocumentModifiedBean document) {
		this.content = content;
		this.document = document;
	}
	
	public String getHtml() {
		
		Parser parser = Parser.builder().build();
		HtmlRenderer renderer = HtmlRenderer.builder()
		        .nodeRendererFactory(new HtmlNodeRendererFactory() {
		            public NodeRenderer create(HtmlNodeRendererContext context) {
		                return new TraceableItemNodeRenderer(context, document);
		            }
		        })
		        .build();
		
		Node document = parser.parse(content);
		String html = renderer.render(document);
		
		return html;
	}

}
