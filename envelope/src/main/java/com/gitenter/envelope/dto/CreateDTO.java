package com.gitenter.envelope.dto;

import com.gitenter.protease.domain.ModelBean;

public interface CreateDTO<ConcreteModelBean extends ModelBean> {
	
	public ConcreteModelBean toBean();
}
