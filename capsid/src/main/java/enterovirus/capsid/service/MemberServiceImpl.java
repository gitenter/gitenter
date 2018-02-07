package enterovirus.capsid.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
/*
 * It seems I should use the Spring annotation of @Transactional,
 * based on:
 * https://stackoverflow.com/questions/26387399/javax-transaction-transactional-vs-org-springframework-transaction-annotation-tr
 * 
 * but this site is using the javax.transaction one:
 * http://www.baeldung.com/spring-security-registration-password-encoding-bcrypt
 * https://github.com/Baeldung/spring-security-registration/blob/2411e74f08077175a15c0c888b9ec63a9e977412/src/main/java/org/baeldung/service/UserService.java
 */
import org.springframework.transaction.annotation.Transactional;

import enterovirus.capsid.dto.MemberDTO;
import enterovirus.protease.database.MemberRepository;
import enterovirus.protease.domain.MemberBean;

@Service
@Transactional
public class MemberServiceImpl {

	@Autowired private MemberRepository memberRepository;
	@Autowired private PasswordEncoder passwordEncoder;
	
	public MemberBean registerNewUserAccount(MemberDTO memberDTO) {// throws EmailExistsException {
//		if (emailExist(memberDTO.getEmail())) {
//			throw new EmailExistsException(
//			  "There is an account with that email adress:" + memberDTO.getEmail());
//		}
		
		MemberBean member = new MemberBean();
		
		member.setUsername(memberDTO.getUsername());
		member.setPassword(passwordEncoder.encode(memberDTO.getPassword()));
		member.setDisplayName(memberDTO.getDisplayName());
		member.setEmail(memberDTO.getEmail());

		return memberRepository.saveAndFlush(member);
	}
}
