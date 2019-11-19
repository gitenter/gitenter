package com.gitenter.capsid.service;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;

import javax.persistence.PersistenceException;

import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.postgresql.util.PSQLException;
import org.postgresql.util.PSQLState;

import com.gitenter.capsid.service.exception.ItemNotUniqueException;
import com.gitenter.protease.domain.auth.PersonBean;
import com.gitenter.protease.domain.auth.RepositoryBean;

public class ExceptionConsumingPipelineTest {

	@Test
	public void testConsumePersistenceExceptionSingleConstrainSuccessfullyRaiseItemNotUniqueException() throws IOException {
		
		PSQLException psqlException = new PSQLException(
				"ERROR: duplicate key value violates unique constraint \"person_username_key\"\n" + 
				"  Detail: Key (username)=(username) already exists.", PSQLState.QUERY_CANCELED);
		ConstraintViolationException constraintViolationException = new ConstraintViolationException(
				"could not execute statement",
				psqlException,
				"person_username_key");
		PersistenceException persistenceException = new PersistenceException(
				"org.hibernate.exception.ConstraintViolationException: could not execute statement",
				constraintViolationException);
		
		PersonBean person = new PersonBean();
	
		ItemNotUniqueException expectedEx = assertThrows(ItemNotUniqueException.class, () -> {
			ExceptionConsumingPipeline.consumePersistenceException(persistenceException, person);
		});
		assertTrue(expectedEx.getMessage().contains("username value breaks SQL constrain."));
	}
	
	@Test
	public void testConsumePersistenceExceptionSingleConstrainBeanDoesNotMatch() throws IOException {
		
		PSQLException psqlException = new PSQLException(
				"ERROR: duplicate key value violates unique constraint \"member_username_key\"\n" + 
				"  Detail: Key (username)=(username) already exists.", PSQLState.QUERY_CANCELED);
		ConstraintViolationException constraintViolationException = new ConstraintViolationException(
				"could not execute statement",
				psqlException,
				"member_username_key");
		PersistenceException persistenceException = new PersistenceException(
				"org.hibernate.exception.ConstraintViolationException: could not execute statement",
				constraintViolationException);
		
		RepositoryBean repository = new RepositoryBean();
		
		assertThrows(PersistenceException.class, () -> {
			ExceptionConsumingPipeline.consumePersistenceException(persistenceException, repository);
		});
	}
	
	@Test
	public void testConsumePersistenceExceptionCompoundConstrainSuccessfullyRaiseItemNotUniqueException() throws IOException {
		
		PSQLException psqlException = new PSQLException(
				"ERROR: duplicate key value violates unique constraint \"repository_organization_id_name_key\"\n" + 
				"  Detail: Key (organization_id, name)=(5, repo) already exists.", PSQLState.QUERY_CANCELED);
		ConstraintViolationException constraintViolationException = new ConstraintViolationException(
				"could not execute statement",
				psqlException,
				"repository_organization_id_name_key");
		PersistenceException persistenceException = new PersistenceException(
				"org.hibernate.exception.ConstraintViolationException: could not execute statement",
				constraintViolationException);
		
		RepositoryBean repositoryBean = new RepositoryBean();
		
		ItemNotUniqueException expectedEx = assertThrows(ItemNotUniqueException.class, () -> {
			ExceptionConsumingPipeline.consumePersistenceException(persistenceException, repositoryBean);
		});
		assertTrue(expectedEx.getMessage().contains("name value breaks SQL constrain."));
	}
	
	@Test
	public void testConsumePersistenceExceptionCompoundConstrainBeanDoesNotMatch() throws IOException {
		
		PSQLException psqlException = new PSQLException(
				"ERROR: duplicate key value violates unique constraint \"repository_organization_id_name_key\"\n" + 
				"  Detail: Key (organization_id, name)=(5, repo) already exists.", PSQLState.QUERY_CANCELED);
		ConstraintViolationException constraintViolationException = new ConstraintViolationException(
				"could not execute statement",
				psqlException,
				"repository_organization_id_name_key");
		PersistenceException persistenceException = new PersistenceException(
				"org.hibernate.exception.ConstraintViolationException: could not execute statement",
				constraintViolationException);
		
		PersonBean person = new PersonBean();
		
		assertThrows(PersistenceException.class, () -> {
			ExceptionConsumingPipeline.consumePersistenceException(persistenceException, person);
		});
	}
}
