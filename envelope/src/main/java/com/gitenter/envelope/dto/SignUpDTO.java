package com.gitenter.envelope.dto;

import java.util.Date;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.security.crypto.password.PasswordEncoder;

import com.gitenter.protease.domain.auth.MemberBean;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignUpDTO extends MemberProfileDTO {
	
	/*
	 * TODO:
	 * For safety concern, consider save it in a character array
	 * rather than an array, and blank it once you are done with it.
	 * Reason: String is immutable and are stored in the Java String 
	 * pool, so it stays in there for an indeterminate period of
	 * time until being garbage collected.
	 */
	@NotNull
	@Size(min=2, max=16)
	private String password;
	
	public MemberBean toMemberBean(PasswordEncoder passwordEncoder) {
		
		MemberBean memberBean = new MemberBean();
		
		memberBean.setUsername(getUsername());
		memberBean.setPassword(passwordEncoder.encode(getPassword()));
		memberBean.setDisplayName(getDisplayName());
		memberBean.setEmail(getEmail());
		memberBean.setRegisterAt(new Date());
		
		return memberBean;
	}

//	public MemberDTO () {
//		
//	}
//	
//	public MemberDTO (MemberBean member) {
//		
//		/*
//		 * Since password cannot be reversely analyzed,
//		 * the corresponding item is just list as blank.
//		 */
//		this.username = member.getUsername();
//		this.displayName = member.getDisplayName();
//		this.email = member.getEmail();
//	}
//	/*
//	 * The inverse "getBean()" is not defined in here,
//	 * as it includes the autowired password encoder, which
//	 * break the POJO rule of this class. 
//	 */
}
