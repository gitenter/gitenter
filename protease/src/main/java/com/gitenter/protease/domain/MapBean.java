package com.gitenter.protease.domain;

public interface MapBean<LeftBean extends ModelBean,RightBean extends ModelBean,MapRole extends Role> extends ModelBean {

	/*
	 * TODO:
	 * 
	 * Define `link()` in this interface. Seems not easy because it is
	 * a static method.
	 */
	
	public boolean isAlterable(String operatorUsername);
}
