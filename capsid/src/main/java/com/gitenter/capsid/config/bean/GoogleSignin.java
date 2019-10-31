package com.gitenter.capsid.config.bean;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GoogleSignin {

	private String clientId;
	
	public String getMetaElement() {
		if (clientId != null) {
			return "<meta name=\"google-signin-client_id\" content=\""+clientId+".apps.googleusercontent.com\">";
		}
		else {
			return null;
		}
	}
	
	public String getButton() {
		if (clientId != null) {
			return "<div class=\"g-signin2\" data-onsuccess=\"onSignIn\"></div>";
		}
		else {
			return null;
		}
	}
}
