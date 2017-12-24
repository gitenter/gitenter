package enterovirus.proteinsistence;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan(basePackages = {"enterovirus.proteinsistence"})
public class Application {
	
	public static void main(String[] args) {
		ApplicationContext context = new AnnotationConfigApplicationContext(Application.class);
		Application p = context.getBean(Application.class);
		p.run();
	}
	
	private void run () {
		
	}
	
}

//@SpringBootApplication
//public class Application {
//
//	private static final Logger log = LoggerFactory.getLogger(Application.class);
//
//	public static void main(String[] args) {
//		SpringApplication.run(Application.class);
//	}
//}
