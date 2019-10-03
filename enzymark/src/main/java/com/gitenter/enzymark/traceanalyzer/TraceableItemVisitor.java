package com.gitenter.enzymark.traceanalyzer;

import org.commonmark.node.AbstractVisitor;
import org.commonmark.node.BulletList;
import org.commonmark.node.ListItem;
import org.commonmark.node.Node;
import org.commonmark.node.Paragraph;
import org.commonmark.node.Text;
import org.commonmark.renderer.html.HtmlRenderer;

import com.gitenter.enzymark.traceparser.TraceableItemParser;

class TraceableItemVisitor extends AbstractVisitor {
	
	private TraceableDocument document;
	
	private final HtmlRenderer defaultRenderer;
	
	TraceableItemVisitor(TraceableDocument document) {
		
		defaultRenderer = HtmlRenderer.builder().build();
		this.document = document;
	}
	
	/*
	 * This is a call for all BulletList nodes.
	 */
	@Override
	public void visit(BulletList bulletList) {
		
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
					
//					System.out.println(listContent.getFirstChild().toString());
					
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
						
						if (parsingResult.isTraceableItem() == true) {
						
							String tag = parsingResult.getTag();
							String content = parsingResult.getContent();
							String[] upstreamItemTags = parsingResult.getUpstreamItemTags();
							
							Node others = listContent.getFirstChild();
							others = others.getNext(); /* Skip the first one which already been writen to HTML */
							for (; others != null; others = others.getNext()) {
								content += defaultRenderer.render(others);
							}
							
							TraceableItem item = new TraceableItem(tag, content, document, upstreamItemTags);
							document.addTraceableItem(item);
						}
					}
				}
			}
		}
	}
}
