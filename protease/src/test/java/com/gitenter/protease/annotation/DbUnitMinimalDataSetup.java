package com.gitenter.protease.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import com.github.springtestdbunit.annotation.DatabaseSetup;

@Retention(RetentionPolicy.RUNTIME)
@DatabaseSetup(connection="schemaAuthDatabaseConnection", value="classpath:dbunit/minimal/auth.xml")
@DatabaseSetup(connection="schemaGitDatabaseConnection", value="classpath:dbunit/minimal/git.xml")
@DatabaseSetup(connection="schemaTraceabilityDatabaseConnection", value="classpath:dbunit/minimal/traceability.xml")
@DatabaseSetup(connection="schemaReviewDatabaseConnection", value="classpath:dbunit/minimal/review.xml")
public @interface DbUnitMinimalDataSetup {
	
}
