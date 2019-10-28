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
import org.commonmark.renderer.html.HtmlRenderer.Builder;
import org.eclipse.jgit.api.errors.GitAPIException;

import com.gitenter.protease.domain.git.DocumentBean;
import com.gitenter.protease.domain.git.FileType;
import com.gitenter.protease.domain.traceability.TraceableDocumentBean;

public class DesignDocumentHtmlGenerator implements HtmlGenerator {

	private DocumentBean document;
	
	/*
	 * Reason for this is to trigger git call in constructor, rather than when
	 * calling `getHtml()`.
	 */
	private String content;
	
	public DesignDocumentHtmlGenerator(DocumentBean document) throws IOException, GitAPIException {
		this.document = document;
		this.content = document.getContent();
	}
	
	public String getHtml() {
		
		if (document.getFileType().equals(FileType.MARKDOWN)) {
			List<Extension> extensions = new ArrayList<Extension>();
			extensions.add(TablesExtension.create());
			extensions.add(StrikethroughExtension.create());
			
			Parser parser = Parser.builder()
					.extensions(extensions)
					.build();
			
			Builder builder = HtmlRenderer.builder().extensions(extensions);
			
			TraceableDocumentBean traceableDocument = document.getTraceableDocument();
			if (traceableDocument != null) {
				builder = builder.nodeRendererFactory(new HtmlNodeRendererFactory() {
					public NodeRenderer create(HtmlNodeRendererContext context) {
						return new TraceableItemNodeRenderer(context, traceableDocument);
					}
				});
			}
			
			builder = builder.nodeRendererFactory(new HtmlNodeRendererFactory() {
				public NodeRenderer create(HtmlNodeRendererContext context) {
					return new ImageNodeRenderer(context, document);
				}
			});
			
			HtmlRenderer renderer = builder.build();
			
			Node node = parser.parse(content);
			String html = renderer.render(node);
			
			return html;
		}
		
		return content;
	}

}
