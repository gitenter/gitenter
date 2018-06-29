package enterovirus.enzymark.htmlgenerator;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Set;

import org.commonmark.node.BulletList;
import org.commonmark.node.ListItem;
import org.commonmark.node.Node;
import org.commonmark.node.Paragraph;
import org.commonmark.node.Text;
import org.commonmark.renderer.NodeRenderer;
import org.commonmark.renderer.html.HtmlNodeRendererContext;
import org.commonmark.renderer.html.HtmlRenderer;
import org.commonmark.renderer.html.HtmlWriter;

import com.gitenter.domain.git.DocumentBean;
import com.gitenter.domain.git.TraceableItemBean;

import enterovirus.enzymark.TraceableItemParser;
import enterovirus.protease.domain.*;

class TraceableItemNodeRenderer implements NodeRenderer {
	
	private final HtmlWriter html;
	private DocumentBean document;
	
	private final HtmlRenderer defaultRenderer;

	TraceableItemNodeRenderer(HtmlNodeRendererContext context, DocumentBean document) {
		
		this.html = context.getWriter();
		
		this.document = document;
		document.buildTraceableItemIndex();
		
		defaultRenderer = HtmlRenderer.builder().build();
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
		html.tag("ul");
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
					 * May be "Text", "StrongEmphasis", ... depend on e.g.
					 * 
					 * > - normal list item
					 * > - **bold** list item
					 * > - *italic* list item
					 */
					if (listContent.getFirstChild() instanceof Text) {
						
						String text = ((Text)(listContent.getFirstChild())).getLiteral();
						TraceableItemParser parsingResult = new TraceableItemParser(text);
						
						if (parsingResult.isTraceableItem() == false) {
							
							html.tag("li");
							html.raw(defaultRenderer.render(listContent));
							html.tag("/li");
							html.line();
						}
						else {

							/*
							 * Handle the special condition that the bullet item is the special
							 * traceable item.
							 */
							String itemTag =  parsingResult.getTag();
							TraceableItemBean traceableItem = document.getTraceableItem(itemTag);
							
							html.tag("li id=\""+itemTag+"\" class=\"traceable-item\"");
							
							html.tag("form method=\"GET\" action=\"#"+itemTag+"\"");
							html.tag("input class=\"original\" type=\"submit\" value=\""+itemTag+"\"");
							html.tag("/form");
							
							for (TraceableItemBean upstreamItem : traceableItem.getUpstreamItems()) {
								
								html.tag("form method=\"GET\" action=\""+getRelativeFilepath(upstreamItem)+"#"+upstreamItem.getItemTag()+"\"");
								html.tag("input class=\"upstream\" type=\"submit\" value=\""+upstreamItem.getItemTag()+"\"");
								html.tag("/form");
							}
							
							for (TraceableItemBean downstreamItem : traceableItem.getDownstreamItems()) {
								
								html.tag("form method=\"GET\" action=\""+getRelativeFilepath(downstreamItem)+"#"+downstreamItem.getItemTag()+"\"");
								html.tag("input class=\"downstream\" type=\"submit\" value=\""+downstreamItem.getItemTag()+"\"");
								html.tag("/form");
							}
							
							html.text(parsingResult.getContent());
							
							Node others = listContent.getFirstChild();
							others = others.getNext(); /* Skip the first one which already been writen to HTML */
							for (; others != null; others = others.getNext()) {
								html.raw(defaultRenderer.render(others));
							}
							
							html.tag("/li");
							html.line();
						}
					}
					else {
						html.tag("li");
						html.raw(defaultRenderer.render(listContent));
						html.tag("/li");
						html.line();
					}
				}
				else {
					/*
					 * Recursively render the nested cases.
					 */
					html.raw(defaultRenderer.render(listContent));
				}
			}
		}
		html.tag("/ul");
		html.line();
	}
	
	private String getRelativeFilepath (TraceableItemBean referredItem) {
		
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
			return referredItem.getDocument().getRelativeFilepath();
		}
		else {
			Path referred = Paths.get(referredItem.getDocument().getRelativeFilepath());
			String relativeFilepath = original.relativize(referred).toString();
			
			return relativeFilepath;
		}
	}

}
