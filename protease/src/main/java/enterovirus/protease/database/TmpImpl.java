package enterovirus.protease.database;

import org.springframework.stereotype.Component;

@Component
public class TmpImpl implements Tmp {

	TmpImpl () {
		
	}
	
	public String find () {
		return "find it!";
	}
}