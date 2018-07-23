package com.gitenter.envelope.service;

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

import com.gitenter.envelope.dto.MemberRegisterDTO;
import com.gitenter.protease.dao.auth.MemberRepository;
import com.gitenter.protease.domain.auth.MemberBean;

@Service
@Transactional
public class AnonymousServiceImpl implements AnonymousService {

	@Autowired private MemberRepository memberRepository;
	@Autowired private PasswordEncoder passwordEncoder;
	
	@Override
	public void signUp(MemberRegisterDTO signUpDTO) {
		
		/*
		 * TODO:
		 * Check if the email address has already been used throughout 
		 * the system.
		 */
		
		MemberBean memberBean = signUpDTO.toMemberBean(passwordEncoder);
		memberRepository.saveAndFlush(memberBean);
		
		/*
		 * TODO:
		 * Send confirmation email, ...
		 */
	}
	
//	public SignUpDTO findDTOById (Integer id) throws IOException {
//		/*
//		 * Note:
//		 * The MemberDTO constructor doesn't copy the password part.
//		 */
//		MemberBean memberBean = memberRepository.findById(id);
//		return memberDTOFromMemberBean(memberBean);
////		return new MemberDTO(memberBean);
//	}
//
//	public SignUpDTO findDTOByUsername (String username) throws IOException {
//		MemberBean memberBean = memberRepository.findByUsername(username);
//		return memberDTOFromMemberBean(memberBean);
////		return new MemberDTO(memberBean);
//	}
//	
//	private SignUpDTO memberDTOFromMemberBean (MemberBean memberBean) {
//		
//		SignUpDTO memberDTO = new SignUpDTO();
//		
//		/*
//		 * Since password cannot be reversely analyzed,
//		 * the corresponding item is just list as blank.
//		 */
//		memberDTO.setUsername(memberBean.getUsername());
//		memberDTO.setDisplayName(memberBean.getDisplayName());
//		memberDTO.setEmail(memberBean.getEmail());
//		
//		return memberDTO;
//	}
}
