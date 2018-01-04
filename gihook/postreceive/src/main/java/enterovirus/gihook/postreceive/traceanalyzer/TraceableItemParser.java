package enterovirus.gihook.postreceive.traceanalyzer;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.MatchResult;

class TraceableItemParser {

	private String tag;
	private List<String> upstreamItemTags = new ArrayList<String>();
	private String content;
	private boolean isTraceableItem = true;
	
	/*
	 * Useful links:
	 * 
	 * Java regular expression special characters:
	 * https://docs.oracle.com/javase/6/docs/api/java/util/regex/Pattern.html
	 * 
	 * Regular expression how to:
	 * http://www.vogella.com/tutorials/JavaRegularExpressions/article.html
	 * 
	 * Scanner how to:
	 * https://docs.oracle.com/javase/1.5.0/docs/api/java/util/Scanner.html
	 * 
	 * A brackets: \\[ and \\]
	 * A curly bracket: \\{ and \\}
	 * A non-whitespace character: \S
	 */
	TraceableItemParser(String lineContent) {
		
		Scanner s = new Scanner(lineContent);

		try {
			/*
			 * The pattern this class currently supporting:
			 * (1) - [itemTag] content
			 */
			try {
				s.findInLine("- \\[(\\S+)\\] (\\S+)");
				MatchResult result = s.match();
				tag = result.group(1);
				content = result.group(2);
			}
			/*
			 * The pattern this class currently supporting:
			 * (2) - [itemTag]{0-or-more-upstreamItemTags} content
			 */
			catch (IllegalStateException e) {
				s.findInLine("- \\[(\\S+)\\]\\{(\\S+)\\} (\\S+)");
				MatchResult result = s.match();
				tag = result.group(1);
				for (String upstreamItemTag : result.group(2).split(",")) {
					upstreamItemTags.add(upstreamItemTag);
				}
				content = result.group(3);			
			}
		}
		/*
		 * If all not match, then this line is not a traceable item at all.
		 */
		catch (IllegalStateException e) {
			isTraceableItem = false;
		}
		
		s.close();
	}

	String getTag() {
		return tag;
	}
	
	List<String> getUpstreamItemTags() {
		return upstreamItemTags;
	}

	String getContent() {
		return content;
	}
	
	boolean isTraceableItem () {
		return isTraceableItem;
	}
}
