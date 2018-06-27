package com.gitenter.domain.git;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class PathBean {

	protected String relativePath;
	
	protected CommitBean commit;
}
