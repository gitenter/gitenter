package enterovirus.immunessh;

import org.springframework.context.annotation.ComponentScan;

@ComponentScan(basePackages = {"enterovirus.protease"})
public class Application {
	
	public static void main (String[] args) throws Exception {

		System.out.println("Hello Immune SSH!");
	}
}
