package enterovirus.capsid.web;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.ui.Model;

import enterovirus.protease.database.*;
import enterovirus.protease.domain.*;

@Controller
public class IndexController {

	@Autowired private MemberRepository memberRepository;
	@Autowired private OrganizationRepository organizationRepository;
	
	@RequestMapping("/")
	public String main(Model model, Authentication authentication) throws Exception {
		
		/*
		 * The organizations the member acts as a manager.
		 */
		MemberBean member = memberRepository.findByUsername(authentication.getName());
		List<OrganizationBean> organizations = member.getOrganizations();
		model.addAttribute("organizations", organizations);
		
		/*
		 * Repositories the member has involved in.
		 */
		List<RepositoryMemberMapBean> repositoryMemberMaps = member.getRepositoryMemberMaps();
		model.addAttribute("repositoryMemberMaps", repositoryMemberMaps);

		return "main";
	}
	
	@RequestMapping(value="/organizations/{organizationId}", method=RequestMethod.GET)
	public String showOrganization (
			@PathVariable Integer organizationId,
			Authentication authentication,
			Model model) throws IOException {
	
		/*
		 * TODO:
		 * For private contents, only users who belong to that
		 * organization can see the materials.
		 */
		
		OrganizationBean organization = organizationRepository.findById(organizationId);
		model.addAttribute("organization", organization);
		
		return "organization";
	}
}
