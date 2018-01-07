package enterovirus.coatmark.parser;

import org.junit.Test;

public class TraceableItemParserTest {

	@Test
	public void test1() {
		TraceableItemParser parsingResult = new TraceableItemParser("[document-1-tag-0001] document-1-0001-content");
		System.out.println("isTraceableItem(): "+parsingResult.isTraceableItem());
		System.out.println("tag: "+parsingResult.getTag());
		System.out.println("content: "+parsingResult.getContent());
		System.out.println("upstreamItems:");
		for (String upstreamItemTag : parsingResult.getUpstreamItemTags()) {
			System.out.println(upstreamItemTag); 
		}
	}
	
	@Test
	public void test2() {
		TraceableItemParser parsingResult = new TraceableItemParser("[document-2-tag-0003]{document-1-tag-0001,document-1-tag-0002} document-2-0003-content");
		System.out.println("isTraceableItem(): "+parsingResult.isTraceableItem());
		System.out.println("tag: "+parsingResult.getTag());
		System.out.println("content: "+parsingResult.getContent());
		System.out.println("upstreamItems:");
		for (String upstreamItemTag : parsingResult.getUpstreamItemTags()) {
			System.out.println(upstreamItemTag); 
		}
	}
	
	@Test
	public void test3() {
		TraceableItemParser parsingResult = new TraceableItemParser("~~~garbage~~~");
		System.out.println("isTraceableItem(): "+parsingResult.isTraceableItem());
	}
}