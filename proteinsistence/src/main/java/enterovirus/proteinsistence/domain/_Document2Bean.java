package enterovirus.proteinsistence.domain;

import java.util.*;
import lombok.*;

@Getter
@Setter
public class _Document2Bean extends BlobBean {
	
	private List<LineContentBean> lineContents = new ArrayList<LineContentBean>();
	
//	private String content;
	
	public void addLineContent(LineContentBean lineContent) {
		lineContents.add(lineContent);
	}
}