package enterovirus.capsid.web;

import javax.validation.Valid;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.gitenter.database.auth.MemberRepository;
import com.gitenter.database.auth.SshKeyRepository;
import com.gitenter.domain.auth.MemberBean;
import com.gitenter.domain.auth.SshKeyBean;

import org.springframework.ui.Model;
import org.springframework.validation.Errors;

import enterovirus.capsid.dto.*;
import enterovirus.capsid.service.MemberService;
import enterovirus.protease.database.*;
import enterovirus.protease.domain.*;

@Controller
@RequestMapping("/settings")
public class SettingsController {

	@Autowired private MemberRepository memberRepository;
	@Autowired private SshKeyRepository sshKeyRepository;
	@Autowired private MemberService memberService;
	@Autowired private PasswordEncoder passwordEncoder;

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
		
		MemberDTO memberDTO = memberService.findDTOByUsername(authentication.getName());
		model.addAttribute("memberDTO", memberDTO);
		
		return "settings/profile";
	}
	
	@RequestMapping(value="/profile", method=RequestMethod.POST)
	public String processUpdateProfile (
			@Valid MemberDTO memberDTOAfterChange, 
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
			 * > Failed to convert value of type 'enterovirus.capsid.dto.MemberDTO' 
			 * > to required type 'java.lang.String'; nested exception is 
			 * > java.lang.IllegalStateException: Cannot convert value of 
			 * > type 'enterovirus.capsid.dto.MemberDTO' to required type 
			 * > 'java.lang.String': no matching editors or conversion strategy found
			 * 
			 * I don't understand why.
			 * 
			 * TODO:
			 * Even if you don't add the attribute, it still displayed
			 * in the redirect page. Need to figure out whether that
			 * addAttribute() is actually needed or not in Spring.
			 */
//			model.addAttribute("memberDTO", memberDTOAfterChange); 
			return "settings/profile";
		}

		MemberBean memberBean = memberRepository.findByUsername(authentication.getName());
		
		/* Since "saveAndFlush()" will decide by itself whether the operation is
		 * INSERT or UPDATE, the bean being actually modified and refreshed should 
		 * be the bean queried from the database, rather than the bean user just
		 * produced. 
		 */
		assert (memberBean.getUsername().equals(memberDTOAfterChange.getUsername()));
		
		memberBean.setDisplayName(memberDTOAfterChange.getDisplayName());
		memberBean.setEmail(memberDTOAfterChange.getEmail());
		memberRepository.saveAndFlush(memberBean);
		
		model.addFlashAttribute("successfulMessage", "Changes has been saved successfully!");
		return "redirect:/settings/profile";
	}
	
	@RequestMapping(value="/account", method=RequestMethod.GET)
	public String showUpdateAccountForm (Model model, Authentication authentication) throws Exception {
		
		MemberDTO memberDTO = memberService.findDTOByUsername(authentication.getName());
		model.addAttribute("memberDTO", memberDTO);
		
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
			@Valid MemberDTO memberDTOAfterChange, 
			Errors errors, 
			@RequestParam(value="old_password") String oldPassword,
			RedirectAttributes model, 
			Authentication authentication) throws Exception {
		
		/*
		 * TODO:
		 * Consider move part of here into MemberService?
		 * It is not straight forward, as it also related to error handling.
		 */
		int expectErrorCount = 0;
		expectErrorCount += errors.getFieldErrorCount("displayName");
		expectErrorCount += errors.getFieldErrorCount("email");
		
		if (errors.getErrorCount() > expectErrorCount) {
			/*
			 * TODO:
			 * Currently cannot map the old values in the redirect page.
			 * With error message (validation in registration works):
			 * > Failed to convert value of type 'enterovirus.capsid.dto.MemberDTO' 
			 * > to required type 'java.lang.String'; nested exception is 
			 * > java.lang.IllegalStateException: Cannot convert value of 
			 * > type 'enterovirus.capsid.dto.MemberDTO' to required type 
			 * > 'java.lang.String': no matching editors or conversion strategy found
			 * 
			 * I don't understand why.
			 */
//			model.addAttribute("memberDTO", memberDTOAfterChange);
			return "settings/account";
		}
		
		MemberBean memberBean = memberRepository.findByUsername(authentication.getName());
		
		if (!passwordEncoder.matches(oldPassword, memberBean.getPassword())) {
			/*
			 * TODO:
			 * Currently cannot map the old values in the redirect page.
			 * With error message (validation in registration works):
			 * > Failed to convert value of type 'enterovirus.capsid.dto.MemberDTO' 
			 * > to required type 'java.lang.String'; nested exception is 
			 * > java.lang.IllegalStateException: Cannot convert value of 
			 * > type 'enterovirus.capsid.dto.MemberDTO' to required type 
			 * > 'java.lang.String': no matching editors or conversion strategy found
			 * 
			 * I don't understand why.
			 */
//			model.addAttribute("memberDTO", memberDTOAfterChange);
			model.addFlashAttribute("errorMessage", "Old password doesn't match!");
			return "redirect:/settings/account";
		}
		
		assert (memberBean.getUsername().equals(memberDTOAfterChange.getUsername()));
		
		memberBean.setPassword(passwordEncoder.encode(memberDTOAfterChange.getPassword()));
		memberRepository.saveAndFlush(memberBean);
		
		model.addFlashAttribute("successfulMessage", "Changes has been saved successfully!");
		return "redirect:/settings/account";
	}
	
	@RequestMapping(value="/ssh", method=RequestMethod.GET)
	public String showSshKeyForm (Model model, Authentication authentication) throws Exception {
		
		MemberBean member = memberRepository.findByUsername(authentication.getName());
		Hibernate.initialize(member.getSshKeys());
		model.addAttribute("member", member);
		
		model.addAttribute("sshKeyFieldBean", new SshKeyFieldDTO());
		return "settings/ssh";
	}
	
	@RequestMapping(value="/ssh", method=RequestMethod.POST)
	public String processAddASshKey (@Valid SshKeyFieldDTO returnValue, Errors errors, 
			Model model, Authentication authentication) throws Exception {
		
		MemberBean member = memberRepository.findByUsername(authentication.getName());
		Hibernate.initialize(member.getSshKeys());
		model.addAttribute("member", member);
		
		if (errors.hasErrors()) {
			
			model.addAttribute("sshKeyFieldBean", returnValue); 
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
			sshKey = new SshKeyBean(returnValue.getValue());
		}
		catch (Exception e) {
			
			model.addAttribute("errorMessage", "The SSH key does not have a valid format.");
			model.addAttribute("sshKeyFieldBean", returnValue);
			return "settings/ssh";
		}
		sshKey.setMember(member);
		
		sshKeyRepository.saveAndFlush(sshKey, member.getUsername());
		return "redirect:/settings/ssh";
	}
}
