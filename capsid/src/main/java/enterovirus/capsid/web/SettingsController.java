package enterovirus.capsid.web;

import javax.validation.Valid;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;

import enterovirus.capsid.web.validation.SshKeyFieldBean;
import enterovirus.protease.database.*;
import enterovirus.protease.domain.*;

@Controller
@RequestMapping("/settings")
public class SettingsController {

	@Autowired private MemberRepository memberRepository;
	@Autowired private SshKeyRepository sshKeyRepository;

	@RequestMapping(method=RequestMethod.GET)
	public String showSettings (Model model) {

		/*
		 * The return view name need to not be directly "settings", because then
		 * if you forward this page to /settings, then you would go into an
		 * infinite loop.
		 * 
		 * Error code looks like this:
		 * 
		 * javax.servlet.ServletException: Circular view path [settings] : would 
		 * dispatch back to the current handler URL [/settings] again. Check your 
		 * ViewResolver setup! (Hint: This may be the result of an unspecified 
		 * view, due to default view name generation.)
		 * 
		 * Therefore, we decide the view name "settings/main", which matches the
		 * JSP view path "/webapp/WEB-INF/views/settings/main.jsp".
		 */
		return "settings/main";
	}
	
	@RequestMapping(value="/profile", method=RequestMethod.GET)
	public String showUpdateProfileForm (Model model, Authentication authentication) throws Exception {
		
		MemberBean member = memberRepository.findByUsername(authentication.getName());
		model.addAttribute("memberBean", member);
		
		return "settings/profile";
	}
	
	@RequestMapping(value="/profile", method=RequestMethod.POST)
	public String processUpdateProfile (
			@Valid MemberBean memberAfterChange, Errors errors, 
			RedirectAttributes model, Authentication authentication) throws Exception {
		
		/* Because an null password field is for sure causes errors, here we need 
		 * skip these associated errors. */
		int expectErrorCount = 0;
		expectErrorCount += errors.getFieldErrorCount("password");
		
		if (errors.getErrorCount() > expectErrorCount) {
			model.addAttribute("memberBean", memberAfterChange); 
			return "settings/profile";
		}

		MemberBean member = memberRepository.findByUsername(authentication.getName());
		
		/* Since "saveAndFlush()" will decide by itself whether the operation is
		 * INSERT or UPDATE, the bean being actually modified and refreshed should 
		 * be the bean queried from the database, rather than the bean user just
		 * produced. 
		 */
		assert (member.getUsername().equals(memberAfterChange.getUsername()));
		
		member.setDisplayName(memberAfterChange.getDisplayName());
		member.setEmail(memberAfterChange.getEmail());
		memberRepository.saveAndFlush(member);
		
		model.addFlashAttribute("successfulMessage", "Changes has been saved successfully!");
		return "redirect:/settings/profile";
	}
	
	@RequestMapping(value="/account", method=RequestMethod.GET)
	public String showUpdateAccountForm (Model model, Authentication authentication) throws Exception {
		
		MemberBean member = memberRepository.findByUsername(authentication.getName());
		model.addAttribute("memberBean", member);
		
		return "settings/account";
	}
	
	@RequestMapping(value="/account", method=RequestMethod.POST)
	public String processUpdateAccount (
			@Valid MemberBean memberAfterChange, Errors errors, 
			@RequestParam(value="old_password") String oldPassword,
			RedirectAttributes model, Authentication authentication) throws Exception {
		
		int expectErrorCount = 0;
		expectErrorCount += errors.getFieldErrorCount("displayName");
		expectErrorCount += errors.getFieldErrorCount("email");
		
		if (errors.getErrorCount() > expectErrorCount) {
			model.addAttribute("memberBean", memberAfterChange);
			return "settings/account";
		}
		
		MemberBean member = memberRepository.findByUsername(authentication.getName());
		
		if (!member.getPassword().equals(oldPassword)) {
			model.addAttribute("memberBean", memberAfterChange);
			model.addFlashAttribute("errorMessage", "Old password doesn't match!");
			return "redirect:/settings/account";
		}
		
		assert (member.getUsername().equals(memberAfterChange.getUsername()));
		
		member.setPassword(memberAfterChange.getPassword());
		memberRepository.saveAndFlush(member);
		
		model.addFlashAttribute("successfulMessage", "Changes has been saved successfully!");
		return "redirect:/settings/account";
	}
	
	@RequestMapping(value="/ssh", method=RequestMethod.GET)
	public String showSshKeyForm (Model model, Authentication authentication) throws Exception {
		
		MemberBean member = memberRepository.findByUsername(authentication.getName());
		Hibernate.initialize(member.getSshKeys());
		model.addAttribute("member", member);
		
		model.addAttribute("sshKeyFieldBean", new SshKeyFieldBean());
		return "settings/ssh";
	}
	
	@RequestMapping(value="/ssh", method=RequestMethod.POST)
	public String processAddASshKey (@Valid SshKeyFieldBean returnValue, Errors errors, 
			Model model, Authentication authentication) throws Exception {
		
		if (errors.hasErrors()) {
			model.addAttribute("sshKeyFieldBean", returnValue); 
			return "settings/ssh";
		}
		
		MemberBean member = memberRepository.findByUsername(authentication.getName());
		
		SshKeyBean sshKey;
		try {
			/*
			 * The "errors" in java validation only checked the key type (by
			 * regular expression). Here gives a complete check.
			 * 
			 * For case it raises "IOException" or "GeneralSecurityException",
			 * that means the key submitted is not valid.  
			 */
			sshKey = new SshKeyBean(returnValue.getValue());
		}
		catch (Exception e) {
			model.addAttribute("errorMessage", "The SSH key does not have a valid format.");
			model.addAttribute("sshKeyFieldBean", returnValue);
			return "settings/ssh";
		}
		sshKey.setMember(member);
		
		sshKeyRepository.saveAndFlush(sshKey);
		return "redirect:/";
	}
}
