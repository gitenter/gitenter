package enterovirus.capsid.web;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import enterovirus.protease.database.*;
import enterovirus.protease.domain.*;
import lombok.Getter;
import lombok.Setter;

@Controller
public class AuthorizationController {

	@Autowired private MemberRepository memberRepository;

	@RequestMapping(value="/register", method=RequestMethod.GET)
	public String registerUser (Model model) {
		
		/* 
		 * The commandName NEED to be the same as the class name,
		 * otherwise the <sf:errors> will not render. 
		 */
		model.addAttribute("memberBean", new MemberBean());
		return "authorization/register";
	}
	
	@RequestMapping(value="/register", method=RequestMethod.POST)
	public String processRegistration (@Valid MemberBean member, Errors errors, Model model) {
		
		if (errors.hasErrors()) {
			/* So <sf:> will render the values in object "member" to the form. */
			model.addAttribute("memberBean", member); 
			return "authorization/register";
		}
		
		memberRepository.saveAndFlush(member);
		return "redirect:/";
	}
	
	@RequestMapping(value="/login", method=RequestMethod.GET)
	public String showLoginForm (
			Model model,
			@RequestParam(value="error", required=false) String error) {
		
		if (error != null) {
			model.addAttribute("message", "Invalid username and password!");
		}

		model.addAttribute("memberLoginBean", new MemberLoginBean());
		return "authorization/login";
	}
	
	/*
	 * This inner class is used for validation only. Persistent is
	 * irrelevant.
	 */
	@Getter
	@Setter
	class MemberLoginBean {
		
		@NotNull
		@Size(min=2, max=16)
		private String username;
		
		@NotNull
		@Size(min=2, max=16)
		private String password;
	}
}
