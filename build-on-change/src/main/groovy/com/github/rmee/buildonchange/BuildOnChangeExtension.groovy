package com.github.rmee.buildonchange

class BuildOnChangeExtension {

	private String referenceBranch = 'master'

	private List<String> rebuildPaths = new ArrayList<>(
			Arrays.asList("build.gradle", "settings.gradle", "gradle.properties",
					"gradlew", "gradlew.bat", "gradle")
	)

	String getReferenceBranch() {
		return referenceBranch
	}

	void setReferenceBranch(String referenceBranch) {
		this.referenceBranch = referenceBranch
	}

	List<String> getRebuildPaths() {
		return rebuildPaths
	}

	void setRebuildPaths(List<String> rebuildPaths) {
		this.rebuildPaths = rebuildPaths
	}
}
