package com.github.rmee.oc;

import com.github.rmee.common.Client;
import com.github.rmee.common.Credentials;
import com.github.rmee.kubectl.KubectlExecResult;
import com.github.rmee.kubectl.KubectlExtensionBase;
import groovy.lang.Closure;
import org.gradle.api.Project;
import org.gradle.internal.os.OperatingSystem;

public class OcExtension extends KubectlExtensionBase {

	@Override
	protected Client createClient() {
		Client client = new Client(this, "oc") {

			@Override
			protected String computeDownloadFileName() {
				OperatingSystem operatingSystem = getOperatingSystem();

				String downloadFileName = "openshift-origin-client-tools-v" + getVersion();
				if (operatingSystem.isLinux()) {
					return downloadFileName + "-linux-64bit.tar.gz";
				}
				else if (operatingSystem.isWindows()) {
					return downloadFileName + "-windows.zip";
				}
				else if (operatingSystem.isMacOsX()) {
					return downloadFileName + "-mac.zip";
				}
				else {
					throw new IllegalStateException("unknown operation system: " + operatingSystem.getName());
				}
			}

			@Override
			protected String computeDownloadUrl(String repository, String downloadFileName) {
				String downloadUrl = repository;
				if (!downloadUrl.endsWith("/")) {
					downloadUrl += "/";
				}

				String version = getVersion();
				int sep = version.indexOf("-");
				if (sep == -1) {
					throw new IllegalArgumentException("expected version " + version + " to be of format x.y.z-abcdefg");
				}
				String baseVersion = version.substring(0, sep);
				downloadUrl += "v" + baseVersion + "/";
				downloadUrl += downloadFileName;
				return downloadUrl;
			}
		};
		client.setDockerized(true);
		client.setImageName("widerin/openshift-cli");
		client.setVersion("v3.9.0");
		client.setRepository("https://github.com/openshift/origin/releases/download/");
		return client;
	}

	@Override
	protected KubectlExecResult createResult(String output) {
		return new OcExecResult(output);
	}

	protected Credentials getCredentialsWithoutInit() {
		return credentials;
	}

	public String getProjectName() {
		init();
		return getNamespace();
	}

	public void setProjectName(String projectName) {
		setNamespace(projectName);
	}

	@Override
	public OcExecResult exec(String command) {
		OcExecSpec spec = new OcExecSpec();
		spec.setCommandLine(command);
		return exec(spec);
	}

	public OcExecResult exec(OcExecSpec spec) {
		return (OcExecResult) super.exec(spec);
	}

	@Override
	protected void setProject(Project project) {
		super.setProject(project);
	}

	public OcExecResult exec(Closure<OcExecSpec> closure) {
		OcExecSpec spec = new OcExecSpec();
		project.configure(spec, closure);
		return exec(spec);
	}
}
