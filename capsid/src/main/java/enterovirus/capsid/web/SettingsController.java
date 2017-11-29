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
	public String showUpdateProfileForm (Model model, Authentication authentication) {
		
		MemberBean member = memberRepository.findByUsername(authentication.getName()).get(0);
		model.addAttribute("memberBean", member);
		
		return "settings/profile";
	}
	
	@RequestMapping(value="/profile", method=RequestMethod.POST)
	public String processUpdateProfile (
			@Valid MemberBean memberAfterChange, Errors errors, 
			RedirectAttributes model, Authentication authentication) {
		
		/* Because an null password field is for sure causes errors, here we need 
		 * skip these associated errors. */
		int expectErrorCount = 0;
		expectErrorCount += errors.getFieldErrorCount("password");
		
		if (errors.getErrorCount() > expectErrorCount) {
			model.addAttribute("memberBean", memberAfterChange); 
			return "settings/profile";
		}

		MemberBean member = memberRepository.findByUsername(authentication.getName()).get(0);
		
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
	public String showUpdateAccountForm (Model model, Authentication authentication) {
		
		MemberBean member = memberRepository.findByUsername(authentication.getName()).get(0);
		model.addAttribute("MemberBean", member);
		
		return "settings/account";
	}
	
	@RequestMapping(value="/account", method=RequestMethod.POST)
	public String processUpdateAccount (
			@Valid MemberBean memberAfterChange, Errors errors, 
			@RequestParam(value="old_password") String oldPassword,
			RedirectAttributes model, Authentication authentication) {
		
		int expectErrorCount = 0;
		expectErrorCount += errors.getFieldErrorCount("displayName");
		expectErrorCount += errors.getFieldErrorCount("email");
		
		if (errors.getErrorCount() > expectErrorCount) {
			model.addAttribute("memberBean", memberAfterChange);
			return "settings/account";
		}
		
		MemberBean member = memberRepository.findByUsername(authentication.getName()).get(0);
		
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
}
