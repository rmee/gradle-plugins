package com.github.rmee.buildonchange

class BuildOnChangeExtension {

	private String referenceBranch = 'master'

	String getReferenceBranch() {
		return referenceBranch
	}

	void setReferenceBranch(String referenceBranch) {
		this.referenceBranch = referenceBranch
	}
}
