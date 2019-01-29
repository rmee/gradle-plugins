package com.github.rmee.jdk.bootstrap;

public class JdkBootstrapExtension {

	private String version;

	private String urlTemplate;

	public JdkBootstrapExtension() {
		useAdoptOpenJdk8("jdk8u202-b08");
	}

	public String getUrlTemplate() {
		return urlTemplate;
	}

	public void setUrlTemplate(String urlTemplate) {
		this.urlTemplate = urlTemplate;
	}

	public void useAdoptOpenJdk8(String version) {
		urlTemplate = "https://github.com/AdoptOpenJDK/openjdk8-binaries/releases/download/jdk" + version + "/OpenJDK8U"
				+ "-jdk_x64_${os}_hotspot_" + version.replace("-", "") +".${suffix}";
		this.version = version;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}
}
