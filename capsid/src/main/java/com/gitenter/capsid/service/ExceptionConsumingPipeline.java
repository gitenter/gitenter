package com.gitenter.capsid.service;

import java.lang.reflect.Field;
import java.sql.SQLException;

import javax.persistence.PersistenceException;

import org.hibernate.exception.ConstraintViolationException;

import com.gitenter.capsid.service.exception.ItemNotUniqueException;
import com.gitenter.protease.domain.ModelBean;

class ExceptionConsumingPipeline {

	static void consumePersistenceException(PersistenceException e, ModelBean bean) throws ItemNotUniqueException,PersistenceException {
		
		if (e.getCause() instanceof ConstraintViolationException) {
			ConstraintViolationException constraintViolationException = (ConstraintViolationException)e.getCause();
			SQLException psqlException = constraintViolationException.getSQLException();
			Field[] attributes =  bean.getClass().getDeclaredFields();
			for (Field attribute : attributes) {
				
				/*
				 * TODO:
				 * Not use bean class field names, but on use the table column name from JPA annotation for matching.
				 * However, do save attribute name as that's what links the DTO and the bean.
				 */
				String tableColumnName = attribute.getName();
				
				/*
				 * TODO:
				 * Right now just check the table attribute name is in unique key name and SQL exception message.
				 * both may or may not be true and may gives fault positive. Also they may not be able to handle 
				 * the cases two+ attributes are involved in the unique constrain. 
				 * this will not break.
				 * > Detail: Key (name)=(org) already exists.
				 * > Key (organization_id, name)=(6, repo) already exists.
				 */ 
				if ((psqlException.getMessage().contains(tableColumnName)) && constraintViolationException.getConstraintName().contains(tableColumnName)) {
					
					/* TODO:
					 * Need to handle the cases unique throughout the system, or for particular
					 * organization/... and probably write it to message. E.g. "repo name already exist throughout the organization!"
					 */
					throw new ItemNotUniqueException(attribute.getName());
				}
			}
		}
		
		throw e;
	}
}
