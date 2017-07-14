package enterovirus.capsid.web;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import enterovirus.gitar.*;

@RestController
public class MainController {	
	
	@RequestMapping("/")
	public String index() {
		return Main.hello()+"\n"+"Hello enterovirus capsid!";
	}
}