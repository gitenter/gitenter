package enterovirus.capsid.web;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;

import enterovirus.capsid.database.*;
import enterovirus.capsid.domain.*;

@Controller
public class AdminController {

	@Autowired private MemberRepository memberRepository;
	@Autowired private OrganizationRepository organizationRepository;

	@RequestMapping(value="/organizations/create", method=RequestMethod.GET)
	public String createOrganization (Model model) {

		model.addAttribute("organizationBean", new OrganizationBean());
		return "admin/create-organization";
	}
	
	@RequestMapping(value="/organizations/create", method=RequestMethod.POST)
	public String processCreation (
			@Valid OrganizationBean organization, 
			Errors errors, 
			Model model,
			Authentication authentication) {
		
		if (errors.hasErrors()) {
			/* So <sf:> will render the values in object "member" to the form. */
			model.addAttribute("organizationBean", organization); 
			return "admin/create-organization";
		}
		
		MemberBean member = memberRepository.findByUsername(authentication.getName()).get(0);
		List<MemberBean> managers = new ArrayList<MemberBean>();
		managers.add(member);
		organization.setManagers(managers);
		
		organizationRepository.saveAndFlush(organization);
		return "redirect:/";
	}
}
