package com.gitenter.enzymark.htmlgenerator;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Set;

import org.commonmark.node.Image;
import org.commonmark.node.Node;
import org.commonmark.node.Text;
import org.commonmark.renderer.NodeRenderer;
import org.commonmark.renderer.html.HtmlNodeRendererContext;
import org.commonmark.renderer.html.HtmlWriter;

import com.gitenter.domain.git.DocumentBean;

class ImageNodeRenderer implements NodeRenderer {
	
	private final HtmlWriter html;
	private DocumentBean document;
	
	ImageNodeRenderer(HtmlNodeRendererContext context, DocumentBean document) {
		
		this.html = context.getWriter();
		this.document = document;
	}

	@Override
	public Set<Class<? extends Node>> getNodeTypes() {
		return Collections.<Class<? extends Node>>singleton(Image.class);
	}

	@Override
	public void render(Node node) {
		
		Image image = (Image) node;
		
		String destination = image.getDestination();
		Path referredPath = Paths.get("documents", "directories", document.getRelativeFilepath()).getParent();
		Path imagePath = Paths.get("blobs", "directories", document.getRelativeFilepath()).getParent().resolve(destination);
		String url = referredPath.relativize(imagePath).toString();
		
		String title = image.getTitle();
		
		String alt;
		if (image.getFirstChild() != null && image.getFirstChild() instanceof Text) {
			alt = ((Text)image.getFirstChild()).getLiteral();
		}
		else {
			alt = "";
		}
		
		html.tag("img src=\""+url+"\" alt=\""+alt+"\" title=\""+title+"\" /");
	}
}
