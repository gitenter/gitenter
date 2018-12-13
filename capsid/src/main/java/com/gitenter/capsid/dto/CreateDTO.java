package com.gitenter.capsid.dto;

import com.gitenter.protease.domain.ModelBean;

public interface CreateDTO<ConcreteModelBean extends ModelBean> {
	
	public ConcreteModelBean toBean();
}
