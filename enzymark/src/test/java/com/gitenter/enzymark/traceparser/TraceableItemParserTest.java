package com.gitenter.enzymark.traceparser;

import static org.junit.Assert.*;

import org.junit.Test;

import com.gitenter.enzymark.traceparser.TraceableItemParser;

public class TraceableItemParserTest {

	@Test
	public void testTraceableItemWithoutDownstream() {
		TraceableItemParser parsingResult = new TraceableItemParser("- [tagWith-_123] Content with !@#$%^&*()[], space and \t in it.");
		assertEquals(parsingResult.isTraceableItem(), true);
		assertEquals(parsingResult.getTag(), "tagWith-_123");
		assertEquals(parsingResult.getContent(), "Content with !@#$%^&*()[], space and \t in it.");
		assertEquals(parsingResult.getUpstreamItemTags(), new String[] {});
	}
	
	@Test
	public void testTraceableItemWithDownstream() {
		TraceableItemParser parsingResult = new TraceableItemParser("[tagWith-_123]{referWith-_123,referWith-_456} Content with !@#$%^&*()[], space and \t in it.");
		assertEquals(parsingResult.isTraceableItem(), true);
		assertEquals(parsingResult.getTag(), "tagWith-_123");
		assertEquals(parsingResult.getContent(), "Content with !@#$%^&*()[], space and \t in it.");
		assertEquals(parsingResult.getUpstreamItemTags(), new String[] {"referWith-_123", "referWith-_456"});
	}
	
	@Test
	public void testTraceableItemWithEmptyDownstream() {
		TraceableItemParser parsingResult = new TraceableItemParser("[tagWith-_123]{} Content with !@#$%^&*()[], space and \t in it.");
		assertEquals(parsingResult.isTraceableItem(), true);
		assertEquals(parsingResult.getTag(), "tagWith-_123");
		assertEquals(parsingResult.getContent(), "Content with !@#$%^&*()[], space and \t in it.");
		assertEquals(parsingResult.getUpstreamItemTags(), new String[] {});
	}
	
	@Test
	public void testNotTraceableItem() {
		TraceableItemParser parsingResult = new TraceableItemParser("~~~garbage~~~");
		assertEquals(parsingResult.isTraceableItem(), false);
	}
	
	/*
	 * TODO:
	 * Add special cases of not well written traceable item cases,
	 * and conditions whether they should be treated as valid or not.
	 */
}