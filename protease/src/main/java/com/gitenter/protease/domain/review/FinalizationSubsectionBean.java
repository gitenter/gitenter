package com.gitenter.protease.domain.review;

import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(schema = "review", name = "finalization_subsection")
public class FinalizationSubsectionBean extends SubsectionBean {

}
