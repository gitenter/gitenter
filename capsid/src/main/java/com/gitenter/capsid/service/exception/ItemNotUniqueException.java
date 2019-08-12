package com.gitenter.capsid.service.exception;

import java.lang.reflect.Field;

import org.hibernate.exception.ConstraintViolationException;
import org.postgresql.util.PSQLException;
import org.springframework.validation.Errors;

import com.gitenter.protease.domain.ModelBean;

public class ItemNotUniqueException extends BackendException {

	private static final long serialVersionUID = 1L;
	
	/*
	 * TODO:
	 * Need to find a more reasonable error code, but need to investigate how to define
	 * an error code (know `Size.java.lang.String`, `Size`, `subscriber.login`, `login`
	 * are existing valid values).
	 */
	private String errorCode = "Size";
	
	private String field;
	private String message;
	
	public ItemNotUniqueException(String message) {
		super(message);
	}
	
	public ItemNotUniqueException(ConstraintViolationException constraintViolationException, ModelBean bean) {
		super("Saving "+bean+" breaks SQL constrain.");
		PSQLException psqlException = (PSQLException)constraintViolationException.getCause();
		Field[] attributes =  bean.getClass().getDeclaredFields();
		for (Field attribute : attributes) {
			/*
			 * TODO:
			 * Should parse `PSQLException` better so if two attributes contain the same string
			 * this will not break.
			 * > Detail: Key (name)=(org) already exists.
			 * > Key (organization_id, name)=(6, repo) already exists.
			 * 
			 * TODO:
			 * Need to handle the cases unique throughout the system, or for particular
			 * organization/... and probably write it to message. E.g. "repo name already exist throughout the organization!"
			 */
			if (psqlException.getMessage().contains(attribute.getName())) {
				field = attribute.getName();
				message = attribute.getName()+" already exist!";
			}
		}
	}
	
	public void addToErrors(Errors errors) {
		errors.rejectValue(field, errorCode, message);
	}
}