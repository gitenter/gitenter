package com.gitenter.enzymark.htmlgenerator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.commonmark.Extension;
import org.commonmark.ext.gfm.strikethrough.StrikethroughExtension;
import org.commonmark.ext.gfm.tables.TablesExtension;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.NodeRenderer;
import org.commonmark.renderer.html.HtmlNodeRendererContext;
import org.commonmark.renderer.html.HtmlNodeRendererFactory;
import org.commonmark.renderer.html.HtmlRenderer;
import org.eclipse.jgit.api.errors.GitAPIException;

import com.gitenter.protease.domain.git.DocumentBean;

public class DesignDocumentHtmlGenerator implements HtmlGenerator {

	private DocumentBean document;
	private String markdownContent;
	
	public DesignDocumentHtmlGenerator(DocumentBean document) throws IOException, GitAPIException {
		this.document = document;
		this.markdownContent = document.getContent();
	}
	
	public String getHtml() {
		
		List<Extension> extensions = new ArrayList<Extension>();
		extensions.add(TablesExtension.create());
		extensions.add(StrikethroughExtension.create());
		
		Parser parser = Parser.builder()
				.extensions(extensions)
				.build();
		HtmlRenderer renderer = HtmlRenderer.builder()
				.extensions(extensions)
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
		
		Node node = parser.parse(markdownContent);
		String html = renderer.render(node);
		
		return html;
	}

}
