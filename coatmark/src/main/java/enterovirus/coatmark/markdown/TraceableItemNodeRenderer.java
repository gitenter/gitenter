package enterovirus.coatmark.markdown;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.commonmark.node.BulletList;
import org.commonmark.node.ListItem;
import org.commonmark.node.Node;
import org.commonmark.node.Paragraph;
import org.commonmark.node.Text;
import org.commonmark.renderer.NodeRenderer;
import org.commonmark.renderer.html.HtmlNodeRendererContext;
import org.commonmark.renderer.html.HtmlWriter;

import enterovirus.coatmark.plaintext.TraceableItemParser;
import enterovirus.protease.domain.*;

public class TraceableItemNodeRenderer implements NodeRenderer {
	
	private final HtmlWriter html;
	private DocumentModifiedBean document;

	TraceableItemNodeRenderer(HtmlNodeRendererContext context, DocumentModifiedBean document) {
		
		this.html = context.getWriter();
		
		this.document = document;
		document.buildTraceableItemIndex();
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
					
					String text = ((Text)(listContent.getFirstChild())).getLiteral();
					TraceableItemParser parser = new TraceableItemParser(text);
					
					if (parser.isTraceableItem() == false) {
						
						/*
						 * Here just shows the normal bubble item.
						 */
						html.tag("ul");
						html.text(text);
						html.tag("/ul");
						html.line();
					}
					else {
						
						String itemTag =  parser.getTag();
						TraceableItemBean traceableItem = document.getTraceableItem(itemTag);
						
						html.tag("ul id=\""+itemTag+"\" class=\"traceable-item\"");
						
						html.tag("form method=\"GET\" action=\"#"+itemTag+"\"");
						html.tag("input class=\"original\" type=\"submit\" value=\""+itemTag+"\"");
						html.tag("/form");
						
						for (TraceabilityMapBean.TraceableItemDocumentPair pair : traceableItem.getUpstreamPairs()) {
							
							html.tag("form method=\"GET\" action=\""+getRelativeFilepath(pair)+"#"+pair.getTraceableItem().getItemTag()+"\"");
							html.tag("input class=\"upstream\" type=\"submit\" value=\""+pair.getTraceableItem().getItemTag()+"\"");
							html.tag("/form");
						}
						
						for (TraceabilityMapBean.TraceableItemDocumentPair pair : traceableItem.getDownstreamPairs()) {
							
							html.tag("form method=\"GET\" action=\""+getRelativeFilepath(pair)+"#"+pair.getTraceableItem().getItemTag()+"\"");
							html.tag("input class=\"downstream\" type=\"submit\" value=\""+pair.getTraceableItem().getItemTag()+"\"");
							html.tag("/form");
						}
						
						html.text(traceableItem.getContent());
						
						html.tag("/ul");
						html.line();
					}
					
					/*
					 * TODO:
					 * If/else the special condition that the bullet item is the special
					 * traceable item.
					 */
					
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
	
	private String getRelativeFilepath (TraceabilityMapBean.TraceableItemDocumentPair pair) {
		
		/*
		 * "getParent()" is needed, because Java doesn't know the original
		 * path is a file rather than a directory. If not, then there will
		 * be one more "../". 
		 */
		Path original = Paths.get(document.getRelativeFilepath()).getParent();
		
		if (original == null) {
			/*
			 * This is for the special case that document is at the root.
			 */
			return pair.getDocument().getRelativeFilepath();
		}
		else {
			Path referred = Paths.get(pair.getDocument().getRelativeFilepath());
			String relativeFilepath = original.relativize(referred).toString();
			
			return relativeFilepath;
		}
	}

}
