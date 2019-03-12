package com.gitenter.capsid.web;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.gitenter.capsid.dto.MemberProfileDTO;
import com.gitenter.capsid.dto.MemberRegisterDTO;
import com.gitenter.capsid.dto.SshKeyFieldDTO;
import com.gitenter.capsid.service.MemberService;
import com.gitenter.protease.domain.auth.MemberBean;
import com.gitenter.protease.domain.auth.SshKeyBean;

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
			@ModelAttribute("memberProfileDTO") @Valid MemberProfileDTO profileAfterChange, 
			Errors errors, 
			RedirectAttributes model, 
			Authentication authentication) throws Exception {
		
		/* Because an null password field is for sure causes errors, here we need 
		 * skip these associated errors. */
		int expectErrorCount = 0;
		expectErrorCount += errors.getFieldErrorCount("password");
		if (errors.getErrorCount() > expectErrorCount) {
			return "settings/profile";
		}

		assert authentication.getName().equals(profileAfterChange.getUsername());
		memberService.updateMember(profileAfterChange);
		
		model.addFlashAttribute("successfulMessage", "Changes has been saved successfully!");
		return "redirect:/settings/profile";
	}
	
	@RequestMapping(value="/account", method=RequestMethod.GET)
	public String showUpdateAccountForm (Model model, Authentication authentication) throws Exception {
		
		/*
		 * Right now the only thing to show is "username". So for the display 
		 * propose of this page, we don't need to load the memberBean at all 
		 * (we can simply use the value of "authentication.getName()"). 
		 * 
		 * However, we do want to use the validation of the corresponding DTO,
		 * as well as its share the password encoder with sign up. That's
		 * the reason we want to load this DTO.
		 */
		MemberRegisterDTO memberRegisterDTO = memberService.getMemberRegisterDTO(authentication);
		model.addAttribute("memberRegisterDTO", memberRegisterDTO);
		
		return "settings/account";
	}

	@RequestMapping(value="/account", method=RequestMethod.POST)
	public String processUpdateAccount (
			/*
			 * "Error" need to go AFTER "@Valid" but BEFORE "@RequestParam" 
			 * attributes, otherwise Spring will directly give 400 error with
			 * message:
			 * > Validation failed for object='XXX'. Error count: XXX
			 * rather than write that information into the "Error" class.
			 */
			@ModelAttribute("memberRegisterDTO") @Valid MemberRegisterDTO registerAfterChange, 
			Errors errors, 
			@RequestParam(value="old_password") String oldPassword,
			RedirectAttributes model, 
			Authentication authentication) throws Exception {
		
		/*
		 * Since there's no hidden input in "account" page HTML, the returned
		 * DTO doesn't have those values. It is dirty on both side, but we'll
		 * keep this dirty part in the controller layer (rather than the view
		 * layer)
		 */
		int expectErrorCount = 0;
		expectErrorCount += errors.getFieldErrorCount("displayName");
		expectErrorCount += errors.getFieldErrorCount("email");
		if (errors.getErrorCount() > expectErrorCount) {
			return "settings/account";
		}
		
		assert authentication.getName().equals(registerAfterChange.getUsername());
		
		if (memberService.updatePassword(registerAfterChange, oldPassword)) {
			model.addFlashAttribute("successfulMessage", "Changes has been saved successfully!");
			return "redirect:/settings/account";
		}
		else {
			model.addFlashAttribute("errorMessage", "Old password doesn't match!");
			return "redirect:/settings/account";
		}
	}
	
	@RequestMapping(value="/ssh", method=RequestMethod.GET)
	public String showSshKeyForm (Model model, Authentication authentication) throws Exception {
		
		MemberBean member = memberService.getMemberByUsername(authentication.getName());
		model.addAttribute("member", member);
		
		model.addAttribute("sshKeyFieldDTO", new SshKeyFieldDTO());
		return "settings/ssh";
	}
	
	@RequestMapping(value="/ssh", method=RequestMethod.POST)
	public String processAddASshKey (
			@Valid SshKeyFieldDTO sshKeyFieldDTO, 
			Errors errors, 
			Model model, 
			Authentication authentication) throws Exception {
		
		MemberBean member = memberService.getMemberByUsername(authentication.getName());
		model.addAttribute("member", member);
		
		if (errors.hasErrors()) {
			
			model.addAttribute("sshKeyFieldDTO", sshKeyFieldDTO); 
			return "settings/ssh";
		}
		
		SshKeyBean sshKey;
		try {
			/*
			 * The "errors" in java validation only checked the key type (by
			 * regular expression). Here gives a complete check.
			 * 
			 * For case it raises "IOException" or "GeneralSecurityException",
			 * that means the key submitted is not valid.  
			 */
			sshKey = sshKeyFieldDTO.toBean();
		}
		catch (Exception e) {
			
			model.addAttribute("errorMessage", "The SSH key does not have a valid format.");
			model.addAttribute("sshKeyFieldDTO", sshKeyFieldDTO);
			return "settings/ssh";
		}
		
		memberService.addSshKey(sshKey, member);
		
		return "redirect:/settings/ssh";
	}
}
