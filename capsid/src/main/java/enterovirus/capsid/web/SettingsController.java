package enterovirus.capsid.web;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;

import enterovirus.capsid.database.*;
import enterovirus.capsid.domain.*;

@Controller
@RequestMapping("/settings")
public class SettingsController {

	@Autowired private MemberRepository memberRepository;
	
	
}
