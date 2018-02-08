package enterovirus.capsid.service;

import java.io.IOException;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import enterovirus.protease.database.*;
import enterovirus.protease.domain.*;

@Service
public class SecurityService {

	@Autowired private MemberRepository memberRepository;
	@Autowired private OrganizationRepository organizationRepository;

	@Transactional
	public boolean checkManagerOfAnOrganization (Authentication authentication, Integer organizationId) {
		
		try {
			MemberBean member = memberRepository.findByUsername(authentication.getName());
			OrganizationBean organization = organizationRepository.findById(organizationId);
			Hibernate.initialize(organization.getManagers());
			
			if (organization.isManagedBy(member.getId())) {
				return true;
			}
			return false;
		}
		catch (IOException e) {
			return false;
		}
	}
}
