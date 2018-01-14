package enterovirus.enzymark.htmlgenerator;

import java.util.Collections;
import java.util.Set;

import org.commonmark.node.Image;
import org.commonmark.node.Node;
import org.commonmark.node.Text;
import org.commonmark.renderer.NodeRenderer;
import org.commonmark.renderer.html.HtmlNodeRendererContext;
import org.commonmark.renderer.html.HtmlWriter;

class ImageNodeRenderer implements NodeRenderer {
	
	private final HtmlWriter html;

	ImageNodeRenderer(HtmlNodeRendererContext context) {
		
		this.html = context.getWriter();
	}

	@Override
	public Set<Class<? extends Node>> getNodeTypes() {
		return Collections.<Class<? extends Node>>singleton(Image.class);
	}

	@Override
	public void render(Node node) {
		
		Image image = (Image) node;
		
		String destination = image.getDestination()+"?blob=true";
		String title = image.getTitle();
		String alt;
		if (image.getFirstChild() != null && image.getFirstChild() instanceof Text) {
			alt = ((Text)image.getFirstChild()).getLiteral();
		}
		else {
			alt = "";
		}
		
		html.tag("img src=\""+destination+"\" alt=\""+alt+"\" title=\""+title+"\" /");
	}
}
