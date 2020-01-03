package com.github.rmee.jdk.bootstrap;

public class JdkBootstrapExtension {

	private String version;

	private String urlTemplate;

	private String windowsName = "windows";

	private String osxName = "mac";

	private String linuxName = "linux";

	public JdkBootstrapExtension() {
		useAdoptOpenJdk8("jdk8u202-b08");
	}

	public String getUrlTemplate() {
		return urlTemplate;
	}

	public void setUrlTemplate(String urlTemplate) {
		this.urlTemplate = urlTemplate;
	}

	public String getWindowsName() {
		return windowsName;
	}

	public void setWindowsName(String windowsName) {
		this.windowsName = windowsName;
	}

	public String getOsxName() {
		return osxName;
	}

	public void setOsxName(String osxName) {
		this.osxName = osxName;
	}

	public String getLinuxName() {
		return linuxName;
	}

	public void setLinuxName(String linuxName) {
		this.linuxName = linuxName;
	}

	public void useAzul(String version) {
		// https://cdn.azul.com/zulu/bin/zulu11.35.15-ca-jdk11.0.5-win_x64.zip
		// https://cdn.azul.com/zulu/bin/zulu13.28.11-ca-jdk13.0.1-win_x64.zip
		// https://cdn.azul.com/zulu/bin/zulu11.35.15-ca-jdk11.0.5-win_x64.zip
		urlTemplate = "https://cdn.azul.com/zulu/bin/" + version + "-${os}_x64.${suffix}";
		windowsName = "win";
		osxName = "macosx";
		this.version = version;
	}

	public void useAdoptOpenJdk8(String version) {
		urlTemplate = "https://github.com/AdoptOpenJDK/openjdk8-binaries/releases/download/jdk" + version + "/OpenJDK8U"
				+ "-jdk_x64_${os}_hotspot_" + version.replace("-", "") +".${suffix}";
		this.version = version;
	}

	public void useAdoptOpenJdk11(String version) {
		urlTemplate = "https://github.com/AdoptOpenJDK/openjdk11-binaries/releases/download/jdk-" + version +
				"/OpenJDK11U-jdk_x64_${os}_hotspot_" + version.replace("+", "_") +".${suffix}";
		this.version = version;
	}

	public void useAdoptOpenJdk13(String version) {
		urlTemplate = "https://github.com/AdoptOpenJDK/openjdk13-binaries/releases/download/jdk-" + version +
				"/OpenJDK13U-jdk_x64_${os}_hotspot_" + version.replace("+", "_")  +".${suffix}";
		this.version = version;
	}




	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}
}
