package com.github.rmee.kubernetes.common;

public class Credentials {

	private String userName;

	private String password;

	private String token;

	private ClientExtensionBase extension;

	public Credentials(ClientExtensionBase extension) {
		this.extension = extension;
	}

	public String getUserName() {
		extension.init();
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		extension.init();
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getToken() {
		extension.init();
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public void init() {

	}
}
