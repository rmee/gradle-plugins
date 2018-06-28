package com.github.rmee.kubectl;

import groovy.json.JsonSlurper;

public class KubectlExecResult {

	private String text;

	protected KubectlExecResult(String text) {
		this.text = text;
	}

	public String getText() {
		return text;
	}

	public Object getJson() {
		return new JsonSlurper().parseText(text);
	}

	public String getProperty(String key) {
		String lower = text.toLowerCase();
		key = key.toLowerCase();
		int sep = lower.indexOf(key + ":");
		if (sep == -1) {
			throw new IllegalStateException("property '" + key + "' not found");
		}
		int endIndex = lower.indexOf("\n", sep);
		return lower.substring(sep + key.length(), endIndex).trim();
	}
}
