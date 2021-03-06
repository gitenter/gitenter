package com.gitenter.capsid.service.exception;

import org.springframework.validation.Errors;

public class ItemNotUniqueException extends BackendException {

	private static final long serialVersionUID = 1L;
	
	/*
	 * TODO:
	 * Need to find a more reasonable error code, but need to investigate how to define
	 * an error code (know `Size.java.lang.String`, `Size`, `subscriber.login`, `login`
	 * are existing valid values).
	 */
	private String errorCode = "Size";
	
	private String beanAttributeName;
	private String message;
	
	public ItemNotUniqueException(String beanAttributeName) {
		super(beanAttributeName+" value breaks SQL constrain.");
		this.beanAttributeName = beanAttributeName;
		
		/*
		 * TODO:
		 * already exist within what scope, e.g. "repo name already exist inside of this organization".
		 */
		this.message = (beanAttributeName+" already exist!");
	}
	
	/*
	 * TODO:
	 * Currently rely on the fact that the DTO class and the domain bean class need to have the 
	 * same field/attribute name. Should be more protective on this, or even test and raise error
	 * if it doesn't work.
	 */
	public void addToErrors(Errors errors) {
		errors.rejectValue(beanAttributeName, errorCode, message);
	}
}