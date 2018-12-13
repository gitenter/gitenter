package com.gitenter.capsid.dto;

import com.gitenter.protease.domain.ModelBean;

public interface ReadDTO<ConcreteModelBean extends ModelBean> {

	/*
	 * TODO:
	 * Is it possible to change this to an abstract class,
	 * and use reflection to define the following methods?
	 */
	public void fillFromBean(ConcreteModelBean bean);
}
