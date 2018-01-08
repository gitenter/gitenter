package enterovirus.coatmark.plaintext;

import org.junit.Test;

import enterovirus.coatmark.plaintext.TraceableItemParser;

public class TraceableItemParserTest {

	@Test
	public void test1() {
		TraceableItemParser parsingResult = new TraceableItemParser("- [tagWith-_123] Content with !@#$%^&*()[], space and \\t in it.");
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
		TraceableItemParser parsingResult = new TraceableItemParser("[tagWith-_123]{referWith-_123,referWith-_456} Content with !@#$%^&*()[], space and \\\\t in it.");
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