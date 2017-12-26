package enterovirus.capsid;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {
		"enterovirus.capsid",
		"enterovirus.protease"})
public class CapsidApplication {

	public static void main(String[] args) {
		SpringApplication.run(CapsidApplication.class, args);
	}
}
