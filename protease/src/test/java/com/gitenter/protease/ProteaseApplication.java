package com.gitenter.protease;

import java.io.IOException;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;

/*
 * This main class has nothing to do with unit tests.
 * If this package is used as a library rather than a
 * stand-alone executive jar, then this class is not
 * needed. 
 */
@ComponentScan
public class ProteaseApplication {

	private void run () throws IOException {	

	}
	
	public static void main (String[] args) throws IOException {
		
		/*
		 * Cannot use the general "GenericApplicationContext"
		 * as it is not auto-closable.
		 */
		try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(ProteaseApplication.class)) {
			ProteaseApplication p = context.getBean(ProteaseApplication.class);
			p.run();
		}
	}
}
