package com.gitenter.capsid.service;

import java.lang.reflect.Field;
import java.sql.SQLException;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PersistenceException;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.exception.ConstraintViolationException;

import com.gitenter.capsid.service.exception.ItemNotUniqueException;
import com.gitenter.protease.domain.ModelBean;

class ExceptionConsumingPipeline {

	static void consumePersistenceException(PersistenceException e, ModelBean bean) throws ItemNotUniqueException,PersistenceException {
		
		if (e.getCause() instanceof ConstraintViolationException) {
			ConstraintViolationException constraintViolationException = (ConstraintViolationException)e.getCause();
			SQLException sqlException = constraintViolationException.getSQLException();
			
			String tableName = bean.getClass().getAnnotation(Table.class).name();
			Field[] attributes =  bean.getClass().getDeclaredFields();
			for (Field attribute : attributes) {

				/*
				 * TODO:
				 * Right now just check the table attribute name is in unique key name and SQL exception message.
				 * both may or may not be true and may gives fault positive. Also they may not be able to handle 
				 * the cases two+ attributes are involved in the unique constrain. 
				 * this will not break.
				 * > Detail: Key (name)=(org) already exists.
				 * > Key (organization_id, name)=(6, repo) already exists.
				 * 
				 * Things get even worse in cases of compound key, e.g. `repository_organization_id_name_key`
				 * and `Key (organization_id, name)=(6, repo) already exists.` both have `organization` and `id` in it.
				 * A better way is probably parse `Key (organization_id, name)=(6, repo) already exists.` and get the
				 * list of `organization_id` and `name` as a word in a WHOLE, and compare them with table names,
				 * but that depend on the detail of PSQL driver error message form. 
				 */ 
				if (attribute.getAnnotation(Id.class) != null) {
					continue;
				}
				if (attribute.getAnnotation(Transient.class) != null) {
					continue;
				}
				if ((attribute.getAnnotation(OneToMany.class) != null)
						|| (attribute.getAnnotation(ManyToOne.class) != null)
						|| (attribute.getAnnotation(ManyToMany.class) != null)) {
					continue;
				}
				
				String tableColumnName;
				Column column = attribute.getAnnotation(Column.class);
				if (column != null) {
					tableColumnName = attribute.getAnnotation(Column.class).name();
				}
				else {
					tableColumnName = attribute.getName();
				}
				if ((sqlException.getMessage().contains(tableColumnName)) && 
						constraintViolationException.getConstraintName().contains(tableName) &&
						constraintViolationException.getConstraintName().contains(tableColumnName)) {
					
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
