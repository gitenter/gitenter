package enterovirus.protease.domain;

import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(schema = "git", name = "git_commit_ignored")
public class CommitIgnoredBean extends CommitBean {

}
