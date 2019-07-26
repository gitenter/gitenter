package com.gitenter.enzymark.htmlgenerator;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import org.commonmark.Extension;
import org.commonmark.ext.gfm.strikethrough.StrikethroughExtension;
import org.commonmark.ext.gfm.tables.TablesExtension;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

/*
 * This class is currently used for generate help documentations.
 * 
 * TODO:
 * Should we make internal links and images working?
 */
public class DefaultHtmlGenerator implements HtmlGenerator {

	private String markdownContent;
	
	public DefaultHtmlGenerator(File markdown) throws IOException {
		byte[] encoded = Files.readAllBytes(markdown.toPath());
		this.markdownContent = new String(encoded, StandardCharsets.UTF_8);
	}
	
	public String getHtml() {
		
		List<Extension> extensions = new ArrayList<Extension>();
		extensions.add(TablesExtension.create());
		extensions.add(StrikethroughExtension.create());
		
		Parser parser = Parser.builder()
				.extensions(extensions)
				.build();
		HtmlRenderer renderer = HtmlRenderer.builder().build();
		
		Node node = parser.parse(markdownContent);
		String html = renderer.render(node);
		
		return html;
	}

}
