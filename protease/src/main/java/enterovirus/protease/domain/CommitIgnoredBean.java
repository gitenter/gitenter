package enterovirus.protease.domain;

import javax.persistence.Entity;
import javax.persistence.Table;

import enterovirus.gitar.wrap.CommitSha;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(schema = "git", name = "git_commit_ignored")
public class CommitIgnoredBean extends CommitBean {

	public CommitIgnoredBean (RepositoryBean repository, CommitSha commitSha) {
		super(repository, commitSha);
	}
}
