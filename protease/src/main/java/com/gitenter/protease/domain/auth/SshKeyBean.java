package com.gitenter.protease.domain.auth;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Base64;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.apache.sshd.common.config.keys.AuthorizedKeyEntry;
import org.apache.sshd.common.config.keys.PublicKeyEntryResolver;

import com.gitenter.protease.domain.ModelBean;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(schema = "auth", name = "ssh_key")
public class SshKeyBean implements ModelBean {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id", updatable=false)
	private Integer id;
	
	@ManyToOne
	@JoinColumn(name="user_id", updatable=false)
	private UserBean user;
	
	@NotNull
	@Column(name="key_type")
	private String keyType;
	
	@NotNull
	@Column(name="key_data")
	private String keyData;

	@Column(name="comment")
	private String comment;
	
	/*
	 * An `authorized_keys` file may also have a set of options
	 * (Refer to: http://man.openbsd.org/sshd.8#AUTHORIZED_KEYS_FILE_FORMAT)
	 * but that is not user defined, so should not be set in here.
	 */
	
	public void setBean(String line) throws GeneralSecurityException, IOException {
		
		AuthorizedKeyEntry entry = AuthorizedKeyEntry.parseAuthorizedKeyEntry(line);
		
		/*
		 * This part is to raise error for invalid key.
		 * It returns a "java.security.PublicKey" object
		 * which have getAlgorithm(), getFormat(), getEncoded(),
		 * but that's not useful for our case.
		 */
		entry.resolvePublicKey(PublicKeyEntryResolver.FAILING);
		
		Base64.Encoder encoder = Base64.getEncoder();
		keyType = entry.getKeyType();
		keyData = encoder.encodeToString(entry.getKeyData());
		comment = entry.getComment();
	}

	@Override
	public String toString() {
		
		String toString = keyType+" "+keyData;
		
		if (comment != null && !comment.equals("")) {
			toString += " "+comment;
		}
		
		return toString;
	}
}
