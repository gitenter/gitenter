package com.gitenter.protease.domain.git;

import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(schema = "git", name = "ignored_commit")
public class IgnoredCommitBean extends CommitBean {

}
