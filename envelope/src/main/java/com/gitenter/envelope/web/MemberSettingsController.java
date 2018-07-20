package com.gitenter.envelope.web;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.gitenter.envelope.dto.MemberProfileDTO;
import com.gitenter.envelope.service.MemberService;
import com.gitenter.protease.domain.auth.MemberBean;

@Controller
@RequestMapping("/settings")
public class MemberSettingsController {
	
	@Autowired MemberService memberService;

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
		
		model.addAttribute("memberProfileDTO", memberService.getMemberProfileDTO(authentication));
		
		return "settings/profile";
	}
	
	@RequestMapping(value="/profile", method=RequestMethod.POST)
	public String processUpdateProfile (
			@Valid MemberProfileDTO profileAfterChange, 
			Errors errors, 
			RedirectAttributes model, 
			Authentication authentication) throws Exception {
		
		/* Because an null password field is for sure causes errors, here we need 
		 * skip these associated errors. */
		int expectErrorCount = 0;
		expectErrorCount += errors.getFieldErrorCount("password");
		
		if (errors.getErrorCount() > expectErrorCount) {
			/*
			 * TODO:
			 * Currently cannot map the old values in the redirect page.
			 * With error message (validation in registration works):
			 * > Failed to convert value of type 'com.gitenter.envelope.dto.MemberProfileDTO' to required type 'java.lang.String'; nested exception is java.lang.IllegalStateException: Cannot convert value of type 'com.gitenter.envelope.dto.MemberProfileDTO' to required type 'java.lang.String': no matching editors or conversion strategy found
			 * 
			 * I don't understand why.
			 * 
			 * However, Even if you don't add the attribute, it still displayed
			 * in the redirect page with the correct error messages. Need to figure 
			 * out whether that addAttribute() is actually needed or not in Spring.
			 */
//			model.addAttribute("memberProfileDTO", profileAfterChange); 
			return "settings/profile";
		}

		memberService.updateMember(authentication, profileAfterChange);
		
		model.addFlashAttribute("successfulMessage", "Changes has been saved successfully!");
		return "redirect:/settings/profile";
	}
}
