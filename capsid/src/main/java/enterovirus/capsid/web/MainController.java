package enterovirus.capsid.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import enterovirus.capsid.database.*;
import enterovirus.capsid.domain.*;

@Controller
public class MainController {
	
	@Autowired private MemberRepository memberRepository;
	@Autowired private OrganizationRepository organizationRepository;
		
	@RequestMapping("/")
	public String main(Model model, Authentication authentication) {
		
		/*
		 * The organizations the member acts as a manager.
		 */
		MemberBean member = memberRepository.findByUsername(authentication.getName()).get(0);
		List<OrganizationBean> organizations = member.getOrganizations();
		model.addAttribute("organizations", organizations);
		
		/*
		 * TODO:
		 * Should also add repositories the member has (some
		 * kind of) access to.
		 */
		
		return "main";
	}
	
	@RequestMapping(value="/organizations/{organizationId}", method=RequestMethod.GET)
	public String showOrganization (
			@PathVariable Integer organizationId,
			Model model) {
	
		/*
		 * TODO:
		 * For private contents, only users who belong to that
		 * organization can see the materials.
		 */
		OrganizationBean organization = organizationRepository.findById(organizationId).get(0);
		model.addAttribute("organization", organization);
		
		return "organization";
	}
}