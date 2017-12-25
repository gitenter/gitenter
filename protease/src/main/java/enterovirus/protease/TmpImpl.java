package enterovirus.protease;

import org.springframework.stereotype.Component;

@Component
public class TmpImpl implements Tmp {

	TmpImpl () {
		
	}
	
	public String find () {
		return "find it!";
	}
}