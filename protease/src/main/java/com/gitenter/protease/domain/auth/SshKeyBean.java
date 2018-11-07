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

import org.apache.sshd.common.config.keys.AuthorizedKeyEntry;
import org.apache.sshd.common.config.keys.PublicKeyEntry;
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
	@JoinColumn(name="member_id", updatable=false)
	private MemberBean member;
	
	@Column(name="key_type")
	private String keyType;
	
	@Column(name="key_data")
	private byte[] keyData;

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
		
		keyType = entry.getKeyType();
		keyData = entry.getKeyData();
		comment = entry.getComment();
	}
	
	public String getKeyDataToString () {
		
		Base64.Encoder encoder = Base64.getEncoder();
		return encoder.encodeToString(keyData);
	}
	
	@Override
	public String toString () {
		
		/*
		 * (1) It is very weird that only "PublicKeyEntry" (rather than 
		 * also "AuthorizedKeyEntry") has constructor.
		 * 
		 * We can also just concatenate the attributes, but Mina SSHD
		 * package does have a better toString() to better cover the
		 * corner conditions we can use.
		 * 
		 * (2) "comment" only appears in "AuthorizedKeyEntry", so we need
		 * to add by ourselves.
		 * 
		 * (3) the "toString()" of "AuthorizedKeyEntry" handle "loginOptions"
		 * in a wrong way (it just return the Map<String,String>), do we cannot
		 * use it to generate the forced commands.
		 */
		PublicKeyEntry entry = new PublicKeyEntry(keyType, keyData);
		
		String toString = entry.toString();
		
		if (comment != null && !comment.equals("")) {
			toString += " "+comment;
		}
		
		return toString;
	}
}
