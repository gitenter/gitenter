package enterovirus.capsid.domain;

import java.util.*;
import lombok.Data;

@Data
public class TextFileBean {
	
	private String filePath;
	private List<LineContentBean> lineContents = new ArrayList<LineContentBean>();
	
//	private String content;
	
	public void addLineContent(LineContentBean lineContent) {
		lineContents.add(lineContent);
	}
}
