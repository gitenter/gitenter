package com.gitenter.protease.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;

import com.gitenter.protease.ProteaseConfig;
import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DbUnitConfiguration;

/*
 * TODO:
 * 
 * Not working. No matter using "RetentionPolicy.RUNTIME" or
 * "RetentionPolicy.CLASS".
 * 
 * "@DbUnitConfiguration" is not working, is because the annotation
 * cannot properly handle bean injection ("Unable to find connection 
 * named schemaAuthDatabaseConnection"). Unknown reasons for others
 * but may need to go inside to see what those annotations are doing.
 * 
 * Also, it seems to setup the below content for each individual test,
 * sometimes it got error for case `databaseConnection` is not a
 * complete set even if the test has no relation to some particular
 * schema at all. Not sure the reason.
 */
@Retention(RetentionPolicy.RUNTIME)
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes=ProteaseConfig.class)
@TestExecutionListeners({
	DependencyInjectionTestExecutionListener.class,
	DirtiesContextTestExecutionListener.class,
	TransactionalTestExecutionListener.class,
	DbUnitTestExecutionListener.class })
@DbUnitConfiguration(databaseConnection={
		"schemaAuthDatabaseConnection", 
		"schemaGitDatabaseConnection", 
		"schemaTraceabilityDatabaseConnection",
		"schemaReviewDatabaseConnection"})
public @interface RunWithSpringDbUnit {

}
