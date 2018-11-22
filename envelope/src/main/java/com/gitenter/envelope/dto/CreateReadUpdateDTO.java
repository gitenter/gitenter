package com.gitenter.envelope.dto;

import com.gitenter.protease.domain.ModelBean;

public interface CreateReadUpdateDTO<ConcreteModelBean extends ModelBean> extends ReadUpdateDTO<ConcreteModelBean> {
	
	public ConcreteModelBean toBean();
}
