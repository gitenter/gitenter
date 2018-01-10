package enterovirus.enzymark.traceanalyzer;

import java.util.ArrayList;
import java.util.List;

import org.commonmark.node.AbstractVisitor;
import org.commonmark.node.BulletList;
import org.commonmark.node.ListItem;
import org.commonmark.node.Node;
import org.commonmark.node.Paragraph;
import org.commonmark.node.Text;

import enterovirus.enzymark.TraceableItemParser;

class TraceableItemVisitor extends AbstractVisitor {

	private List<TraceableItem> traceableItems = new ArrayList<TraceableItem>();
	private TraceableDocument document;
	
	TraceableItemVisitor (TraceableDocument document) {
		this.document = document;
	}
	
	/*
	 * This is a call for all BulletList nodes.
	 */
	@Override
	public void visit (BulletList bulletList) {
		
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
					TraceableItemParser parsingResult = new TraceableItemParser(text);
					
					if (parsingResult.isTraceableItem() == true) {
					
						TraceableItem item = new TraceableItem(parsingResult, document);
						traceableItems.add(item);
					}
				}
			}
		}
	}

	public List<TraceableItem> getTraceableItems() {
		return traceableItems;
	}
}
