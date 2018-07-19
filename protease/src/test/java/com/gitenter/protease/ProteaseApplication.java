package com.gitenter.protease;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;

import com.gitenter.protease.dao.auth.OrganizationRepository;
import com.gitenter.protease.domain.auth.OrganizationBean;

/*
 * This main class has nothing to do with unit tests.
 * If this package is used as a library rather than a
 * stand-alone executive jar, then this class is not
 * needed. 
 */
@ComponentScan
public class ProteaseApplication {
	
	@Autowired private OrganizationRepository organizationRepository;
	
	private void run () throws IOException {	
		OrganizationBean organization = organizationRepository.findByName("org1").get(0);
		System.out.println(organization.getDisplayName());
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
