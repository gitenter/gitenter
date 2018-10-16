package enterovirus.gihook.postreceive;

import com.gitenter.gitar.GitCommit;
import com.gitenter.protease.domain.auth.RepositoryBean;
import com.gitenter.protease.domain.git.CommitBean;
import com.gitenter.protease.domain.git.ValidCommitBean;

public class CommitBeanFactory {

	public CommitBean getCommit(RepositoryBean repository, GitCommit gitCommit) {
		
		return new ValidCommitBean();
	}
}
