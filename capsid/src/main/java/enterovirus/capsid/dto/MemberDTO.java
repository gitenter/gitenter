package enterovirus.capsid.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.Email;

import com.gitenter.domain.settings.MemberBean;

import lombok.Getter;
import lombok.Setter;

/*
 * In general, the bean validation are in domain classes
 * (enterovirus.protease.domain package) rather than separated
 * DTO classes.
 * 
 * However, as we need to handle password encoding of the member
 * bean (in MemberService), we split the validation and the 
 * persistent parts of this bean.
 */
@Getter
@Setter
public class MemberDTO {

	@NotNull
	@Size(min=2, max=16)
	private String username;
	
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

	@NotNull
	@Size(min=2, max=64)
	private String displayName;
	
	/*
	 * With @Email annotation, this column is not nullable.
	 */
	@Email
	private String email;

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
