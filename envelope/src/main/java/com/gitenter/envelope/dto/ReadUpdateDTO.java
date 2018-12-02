package com.gitenter.envelope.dto;

import com.gitenter.protease.domain.ModelBean;

public interface ReadUpdateDTO<ConcreteModelBean extends ModelBean> extends ReadDTO<ConcreteModelBean> {

	/*
	 * TODO:
	 * Is it possible to change this to an abstract class,
	 * and use reflection to define the following methods?
	 */
	public void updateBean(ConcreteModelBean bean);
}
