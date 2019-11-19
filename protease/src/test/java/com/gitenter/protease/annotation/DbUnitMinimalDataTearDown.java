package com.gitenter.protease.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import com.github.springtestdbunit.annotation.DatabaseTearDown;

@Retention(RetentionPolicy.RUNTIME)
@DatabaseTearDown(connection="schemaReviewDatabaseConnection", value="classpath:dbunit/minimal/review.xml")
@DatabaseTearDown(connection="schemaTraceabilityDatabaseConnection", value="classpath:dbunit/minimal/traceability.xml")
@DatabaseTearDown(connection="schemaGitDatabaseConnection", value="classpath:dbunit/minimal/git.xml")
@DatabaseTearDown(connection="schemaAuthDatabaseConnection", value="classpath:dbunit/minimal/auth.xml")
public @interface DbUnitMinimalDataTearDown {
	
}
