package com.gitenter.capsid.web;

import javax.servlet.http.HttpServletRequest;
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

import com.gitenter.capsid.dto.SshKeyFieldDTO;
import com.gitenter.capsid.dto.UserProfileDTO;
import com.gitenter.capsid.dto.UserRegisterDTO;
import com.gitenter.capsid.service.UserService;
import com.gitenter.protease.domain.auth.SshKeyBean;
import com.gitenter.protease.domain.auth.UserBean;

import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/settings")
@Slf4j
public class UserSettingsController {
	
	private final UserService userService;
	
	@Autowired
	public UserSettingsController(UserService userService) {
		this.userService = userService;
	}

	@RequestMapping(method=RequestMethod.GET)
	public String showSettings(Model model) {

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
	public String showUpdateProfileForm(Model model, Authentication authentication) throws Exception {
		
		model.addAttribute("userProfileDTO", userService.getUserProfileDTO(authentication));
		
		return "settings/profile";
	}
	
	@RequestMapping(value="/profile", method=RequestMethod.POST)
	public String processUpdateProfile(
			@ModelAttribute("userProfileDTO") @Valid UserProfileDTO profileAfterChange, 
			Errors errors, 
			RedirectAttributes model) throws Exception {
		
		/* Because an null password field is for sure causes errors, here we need 
		 * skip these associated errors. */
		int expectErrorCount = 0;
		expectErrorCount += errors.getFieldErrorCount("password");
		if (errors.getErrorCount() > expectErrorCount) {
			return "settings/profile";
		}

		/*
		 * No need to do the following, as it can be done through `@PreAuthorize`
		 * in the service layer.
		 * > assert authentication.getName().equals(profileAfterChange.getUsername());
		 */
		userService.updateUser(profileAfterChange);
		log.debug("User changed profile. New profile: "+profileAfterChange);
		
		model.addFlashAttribute("successfulMessage", "Changes has been saved successfully!");
		return "redirect:/settings/profile";
	}
	
	@RequestMapping(value="/account/password", method=RequestMethod.GET)
	public String showChangePassword(Model model, Authentication authentication) throws Exception {
		
		/*
		 * Right now the only thing to show is "username". So for the display 
		 * propose of this page, we don't need to load the userBean at all 
		 * (we can simply use the value of "authentication.getName()"). 
		 * 
		 * However, we do want to use the validation of the corresponding DTO,
		 * as well as its share the password encoder with sign up. That's
		 * the reason we want to load this DTO.
		 */
		UserRegisterDTO userRegisterDTO = userService.getUserRegisterDTO(authentication);
		model.addAttribute("userRegisterDTO", userRegisterDTO);
		
		return "settings/account/password";
	}

	@RequestMapping(value="/account/password", method=RequestMethod.POST)
	public String processUpdatePassword(
			/*
			 * "Error" need to go AFTER "@Valid" but BEFORE "@RequestParam" 
			 * attributes, otherwise Spring will directly give 400 error with
			 * message:
			 * > Validation failed for object='XXX'. Error count: XXX
			 * rather than write that information into the "Error" class.
			 */
			@ModelAttribute("userRegisterDTO") @Valid UserRegisterDTO registerAfterChange, 
			Errors errors, 
			@RequestParam(value="old_password") String oldPassword,
			RedirectAttributes model) throws Exception {
		
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
			return "settings/account/password";
		}
		
		if (userService.updatePassword(registerAfterChange, oldPassword)) {
			log.info("User changed password: "+registerAfterChange.getUsername());
			
			model.addFlashAttribute("successfulMessage", "Changes has been saved successfully!");
			return "redirect:/settings/account/password";
		}
		else {
			model.addFlashAttribute("errorMessage", "Old password doesn't match!");
			return "redirect:/settings/account/password";
		}
	}
	
	@RequestMapping(value="/account/delete", method=RequestMethod.GET)
	public String showDeleteAccount(Model model) throws Exception {
		
		return "settings/account/delete";
	}
	
	@RequestMapping(value="/account/delete", method=RequestMethod.POST)
	public String processDeleteAccount(
			Authentication authentication,
			@RequestParam(value="password") String password,
			RedirectAttributes model,
			HttpServletRequest request) throws Exception {
		
		if (userService.deleteUser(authentication.getName(), password)) {
			request.logout();
			
			log.info("User account deleted. Username: "+authentication.getName()+". IP: "+request.getRemoteAddr());
			
			/*
			 * TODO:
			 * Message for successful delete account.
			 */
			return "redirect:/";
		}
		else {
			model.addFlashAttribute("errorMessage", "Password doesn't match!");
			return "redirect:/settings/account/delete";
		}	
	}
	
	@RequestMapping(value="/ssh", method=RequestMethod.GET)
	public String showSshKeyForm(Model model, Authentication authentication) throws Exception {
		
		UserBean user = userService.getUserByUsername(authentication.getName());
		model.addAttribute("user", user);
		
		model.addAttribute("sshKeyFieldDTO", new SshKeyFieldDTO());
		return "settings/ssh";
	}
	
	@RequestMapping(value="/ssh", method=RequestMethod.POST)
	public String processAddASshKey(
			@Valid SshKeyFieldDTO sshKeyFieldDTO, 
			Errors errors, 
			Model model, 
			Authentication authentication) throws Exception {
		
		UserBean user = userService.getUserByUsername(authentication.getName());
		model.addAttribute("user", user);
		
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
			
			model.addAttribute("errorMessage", "The SSH key does not have a valid format!");
			model.addAttribute("sshKeyFieldDTO", sshKeyFieldDTO);
			return "settings/ssh";
		}
		
		userService.addSshKey(sshKey, user);
		
		return "redirect:/settings/ssh";
	}
}
