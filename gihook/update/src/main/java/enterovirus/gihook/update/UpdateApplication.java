package enterovirus.gihook.update;

import java.io.File;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;

import enterovirus.gitar.GitFolderStructure;
import enterovirus.gitar.GitSource;
import enterovirus.gitar.wrap.CommitSha;
import enterovirus.protease.database.*;
import enterovirus.protease.domain.*;

/*
 * This main class has nothing to do with unit tests.
 * If this package is used as a library rather than a
 * stand-alone executive jar, then this class is not
 * needed. 
 */
@ComponentScan(basePackages = {
		"enterovirus.protease.config",
		"enterovirus.protease.database",
		"enterovirus.protease.domain"})
public class UpdateApplication {
	
	public static void main (String[] args) throws IOException {

		String branchName = args[0];
		String oldCommitSha = args[1];
		String newCommitSha = args[2];
	}
}
