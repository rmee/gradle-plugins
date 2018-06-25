package com.github.rmee.kubectl;

import com.github.rmee.common.Client;
import org.gradle.internal.os.OperatingSystem;

public class KubectlExtension extends KubectlExtensionBase {

	@Override
	protected Client createClient() {
		Client client = new Client(this, "kubectl") {

			@Override
			protected String computeDownloadFileName() {
				OperatingSystem operatingSystem = getOperatingSystem();
				if (operatingSystem.isWindows()) {
					return "kubectl.exe";
				}
				return "kubectl";
			}

			@Override
			protected String computeDownloadUrl(String repository, String downloadFileName) {
				String downloadUrl = repository;
				if (!downloadUrl.endsWith("/")) {
					downloadUrl += "/";
				}
				downloadUrl += "v" + getVersion() + "/bin/";

				OperatingSystem operatingSystem = getOperatingSystem();
				if (operatingSystem.isLinux()) {
					return downloadUrl + "linux/amd64/" + downloadFileName;
				} else if (operatingSystem.isWindows()) {
					return downloadUrl + "windows/amd64/" + downloadFileName;
				} else if (operatingSystem.isMacOsX()) {
					return downloadUrl + "darwin/amd64/" + downloadFileName;
				} else {
					throw new IllegalStateException("unknown operation system: " + operatingSystem.getName());
				}
			}

		};
		client.setVersion("2.8.2");
		client.setImageName("dtzar/helm-kubectl");
		client.setRepository("https://storage.googleapis.com/kubernetes-release/release/");
		client.setDockerized(true);
		return client;
	}

	@Override
	public KubectlExecResult exec(KubectlExecSpec execSpec) {
		return super.exec(execSpec);
	}
}