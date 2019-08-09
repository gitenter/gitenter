package com.gitenter.capsid.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.gitenter.capsid.service.exception.InvalidDataStateException;
import com.gitenter.capsid.service.exception.InvalidOperationException;
import com.gitenter.capsid.service.exception.ResourceNotFoundException;
import com.gitenter.capsid.service.exception.UnreachableException;

@ControllerAdvice
public class ExceptionHandlingController {
	
	private static final Logger logger = LoggerFactory.getLogger(ExceptionHandlingController.class);
	
	private void errorLogging(Exception e) {
		logger.error("Raising exception "+e.getClass().getSimpleName()+" with message: "+e.getMessage());
	}
	
	private void warningLogging(Exception e) {
		logger.warn("Raising exception "+e.getClass().getSimpleName()+" with message: "+e.getMessage());
	}
	
	/*
	 * TODO:
	 * Looks like it is very hard to test exceptional handling logic. I tried to define a controller
	 * (both as a normal class or inner class of the test method in the `src/test` folder) which 
	 * raises an exception, but it cannot be seen from `@ControllerAdvice`.
	 */

	/*
	 * Front-end exceptions
	 */
	@ExceptionHandler(UnreachableException.class)
	public String unreachableErrorPage(Exception e) {
		errorLogging(e);
		return "exception-handling/something-has-gone-wrong";
	}
	
	@ExceptionHandler(InvalidOperationException.class)
	public String invalidOperationWarningPage(Exception e) {
		warningLogging(e);
		return "exception-handling/invalid-operation";
	}
	
	/*
	 * Back-end exceptions
	 */
	@ExceptionHandler(InvalidDataStateException.class)
	public String invalidDataStateErrorPage(Exception e) {
		errorLogging(e);
		return "exception-handling/something-has-gone-wrong";
	}
	
	@ExceptionHandler(ResourceNotFoundException.class)
	public String resourceNotFoundErrorPage(Exception e) {
		warningLogging(e);
		return "exception-handling/not-found";
	}
}
