package com.github.rmee.jpa.schemagen;

public class LiquibaseExtension {

	private String fileName = "liquibase-changelog.xml";

	private String user;

	public String getUser() {
		if (user == null) {
			user = System.getenv("USER");
		}
		if (user == null) {
			user = System.getenv("USERNAME");
		}
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
}
