package com.gitenter.protease.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import com.github.springtestdbunit.annotation.DatabaseSetup;

/*
 * TODO:
 * 
 * There's a bug on DbUnit setup, that right now we setup the primary 
 * key manually in the XML data file. However, DbUnit doesn't reset
 * the sequence (e.g. `ALTER SEQUENCE schema_name.table_name_id_seq 
 * RESTART WITH 2;`), so in the first round of unit tests (after database
 * is reset), the insert related ones will fail. It will pass after 2-3
 * rounds, through.
 * 
 * May refer:
 * https://stackoverflow.com/questions/20607704/reset-sequence-in-dbunit
 */
@Retention(RetentionPolicy.RUNTIME)
@DatabaseSetup(connection="schemaAuthDatabaseConnection", value="classpath:dbunit/minimal/auth.xml")
@DatabaseSetup(connection="schemaGitDatabaseConnection", value="classpath:dbunit/minimal/git.xml")
@DatabaseSetup(connection="schemaReviewDatabaseConnection", value="classpath:dbunit/minimal/review.xml")
public @interface DbUnitMinimalDataSetup {
	
}
