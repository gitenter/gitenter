package com.gitenter.capsid.service;

import java.io.IOException;

import javax.persistence.PersistenceException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.gitenter.capsid.dto.MemberRegisterDTO;
import com.gitenter.capsid.service.exception.UsernameNotUniqueException;
import com.gitenter.protease.dao.auth.MemberRepository;
import com.gitenter.protease.domain.auth.MemberBean;

@Service
public class AnonymousServiceImpl implements AnonymousService {

	@Autowired private MemberRepository memberRepository;
	@Autowired private PasswordEncoder passwordEncoder;
	
	/*
	 * TODO:
	 * Transaction setup. Notice that a simple `@Transactional` will cause the application unable
	 * to catch `UsernameNotUniqueException` to redirect to the registration page. Optional in here
	 * since even if we failed to send email the user should still be treated as registered.
	 * > o.s.t.i.TransactionInterceptor : Application exception overridden by commit exception
	 */
	@Override
	public void signUp(MemberRegisterDTO memberRegisterDTO) throws IOException {
		
		/*
		 * TODO:
		 * Check if the email address has already been used throughout 
		 * the system.
		 */
		
		MemberBean memberBean = memberRegisterDTO.toBean(passwordEncoder);
		try {
			memberRepository.saveAndFlush(memberBean);
		}
		catch(PersistenceException e) {
			/*
			 * TODO:
			 * This logic just means one constrain is broken. It doesn't always mean the username
			 * unique constrain is broken.
			 */
			if (e.getMessage().contains("ConstraintViolationException")) {
				throw new UsernameNotUniqueException(memberBean);
			}
			throw e;
		}
		
		/*
		 * TODO:
		 * Send confirmation email, ...
		 */
	}
}
