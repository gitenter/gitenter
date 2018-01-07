package enterovirus.coatmark;

import java.util.Collections;
import java.util.Set;

import org.commonmark.node.BulletList;
import org.commonmark.node.IndentedCodeBlock;
import org.commonmark.node.ListItem;
import org.commonmark.node.Node;
import org.commonmark.node.Paragraph;
import org.commonmark.node.Text;
import org.commonmark.renderer.NodeRenderer;
import org.commonmark.renderer.html.HtmlNodeRendererContext;
import org.commonmark.renderer.html.HtmlWriter;

public class TraceableItemNodeRenderer implements NodeRenderer {
	
	private final HtmlWriter html;

	TraceableItemNodeRenderer(HtmlNodeRendererContext context) {
		this.html = context.getWriter();
	}

	@Override
	public Set<Class<? extends Node>> getNodeTypes() {
		return Collections.<Class<? extends Node>>singleton(BulletList.class);
	}

	/*
	 * TODO:
	 * Maybe a better way is not to rewrite the "BulletList" render,
	 * and if/else the normal bullet cases, but to define another node
	 * and to handle that.
	 */
	@Override
	public void render(Node node) {
		
		BulletList bulletList = (BulletList) node;
		html.line();
		html.tag("li");
		html.line();
		/* 
		 * As all the BulletList's children should always be ListItem 
		 */ 
		for (ListItem listItem = (ListItem)bulletList.getFirstChild(); 
				listItem != null; listItem = (ListItem)listItem.getNext()) {
			
			/*
			 * ListItem's children may be
			 * (1) Paragraph
			 * (2) BulletList
			 * (3) OrderedList
			 * 
			 * We only need to handle the first case in a special way.
			 */
			for (Node listContent = listItem.getFirstChild();
					listContent != null; listContent = listContent.getNext()) {
				
				if (listContent instanceof Paragraph) {
					
					/*
					 * TODO:
					 * If/else the special condition that the bullet item is the special
					 * traceable item.
					 */
					html.tag("ul");
					html.text(((Text)(listContent.getFirstChild())).getLiteral());
					html.tag("/ul");
					html.line();
				}
				else {
					/*
					 * Recursively render the nested cases.
					 */
					render(listContent);
				}
			}
		}
		html.tag("/li");
		html.line();
	}

}
