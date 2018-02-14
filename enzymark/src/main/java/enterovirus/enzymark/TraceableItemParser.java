package enterovirus.enzymark;

import java.util.Scanner;
import java.util.regex.MatchResult;

import lombok.Getter;

@Getter
public class TraceableItemParser {

	private String tag;
	private String[] upstreamItemTags = new String[] {};
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
	public TraceableItemParser(String text) {
		
		Scanner s = new Scanner(text);

		try {
			/*
			 * The pattern this class currently supporting:
			 * (1) - [itemTag] content
			 * 
			 * Note:
			 * ([ \\t\\S]*) rather than ([ \\t\\S]+)
			 * Because maybe for cases like "- [itemTag] **Bold**..." after
			 * the parser the input is really just only "- [itemTag] "
			 */
			try {
				s.findInLine("\\[(\\S+)\\] ([ \\t\\S]*)");
				MatchResult result = s.match();
				tag = result.group(1);
				content = result.group(2);
			}
			/*
			 * The pattern this class currently supporting:
			 * (2) - [itemTag]{0-or-more-upstreamItemTags} content
			 * 
			 * It is "(\\S*)\\" rather than (\\S+)\\
			 * for cases with no reference like "- [tag]{}"
			 */
			catch (IllegalStateException e) {
				s.findInLine("\\[(\\S+)\\]\\{(\\S*)\\} ([ \\t\\S]*)");
				MatchResult result = s.match();
				tag = result.group(1);
				if (result.group(2).length() != 0) {
					upstreamItemTags = result.group(2).split(",");
				}
				else {
					upstreamItemTags = new String[] {};
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
	
	public boolean isTraceableItem () {
		return isTraceableItem;
	}
}
