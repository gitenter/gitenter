package com.gitenter.capsid.service;

import java.io.IOException;

import javax.persistence.PersistenceException;

import org.hibernate.exception.ConstraintViolationException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.postgresql.util.PSQLException;
import org.postgresql.util.PSQLState;

import com.gitenter.capsid.service.exception.ItemNotUniqueException;
import com.gitenter.protease.domain.auth.MemberBean;
import com.gitenter.protease.domain.auth.OrganizationBean;

public class ExceptionConsumingPipelineTest {
	
	@Rule 
	public ExpectedException expectedEx = ExpectedException.none();

	@Test
	public void testConsumePersistenceExceptionSuccessfullyRaiseItemNotUniqueException() throws IOException {
		expectedEx.expect(ItemNotUniqueException.class);
	    expectedEx.expectMessage("username value breaks SQL constrain.");
		
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
		
		MemberBean memberBean = new MemberBean();
		ExceptionConsumingPipeline.consumePersistenceException(persistenceException, memberBean);
	}
	
	@Test(expected = PersistenceException.class)
	public void testConsumePersistenceExceptionBeanDoesNotMatch() throws IOException {
		
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
		
		OrganizationBean organizationBean = new OrganizationBean();
		ExceptionConsumingPipeline.consumePersistenceException(persistenceException, organizationBean);
	}
}
