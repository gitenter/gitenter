package enterovirus.capsid.web;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import enterovirus.protease.database.*;
import enterovirus.protease.domain.*;

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
			Model model) throws IOException {
	
		/*
		 * TODO:
		 * For private contents, only users who belong to that
		 * organization can see the materials.
		 */
		Optional<OrganizationBean> organizations = organizationRepository.findById(organizationId);
		if (!organizations.isPresent()) {
			throw new IOException ("organizationId does not exist!");
		}
		OrganizationBean organization = organizations.get();
		
		Hibernate.initialize(organization.getManagers());
		
		model.addAttribute("organization", organization);
		return "organization";
	}
}