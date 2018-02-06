package enterovirus.capsid.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;


@Configuration
public class WebConfig {

	@Profile("sts")
	@Bean
	public WebSource stsWebSource() {
		
		WebSource webSource = new WebSource();
		webSource.setDomainName("localhost");
		return webSource;
	}
	
	@Profile("localhost")
	@Bean
	public WebSource localhostWebSource() {
		
		WebSource webSource = new WebSource();
		webSource.setDomainName("localhost");
		return webSource;
	}
	
	@Profile("production")
	@Bean
	public WebSource productionWebSource() {
		
		WebSource webSource = new WebSource();
		webSource.setDomainName("52.41.66.37");
		return webSource;
	}
}
