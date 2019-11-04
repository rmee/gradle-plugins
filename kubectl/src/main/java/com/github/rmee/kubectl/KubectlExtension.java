package com.github.rmee.kubectl;

import com.github.rmee.cli.base.Cli;
import com.github.rmee.cli.base.ExecResult;
import com.github.rmee.cli.base.internal.CliDownloadStrategy;
import groovy.lang.Closure;
import org.gradle.internal.os.OperatingSystem;

public class KubectlExtension extends KubectlExtensionBase {

	@Override
	protected Cli createClient() {
		Cli cli = new Cli(this, "kubectl", new CliDownloadStrategy() {
			@Override
			public String computeDownloadFileName(Cli cli) {
				OperatingSystem operatingSystem = cli.getOperatingSystem();
				if (operatingSystem.isWindows()) {
					return "kubectl.exe";
				}
				return "kubectl";
			}

			@Override
			public String computeDownloadUrl(Cli cli, String repository, String downloadFileName) {
				String downloadUrl = repository;
				if (!downloadUrl.endsWith("/")) {
					downloadUrl += "/";
				}
				downloadUrl += "v" + cli.getVersion() + "/bin/";

				OperatingSystem operatingSystem = cli.getOperatingSystem();
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
		});
		cli.setVersion("2.9.1");
		cli.setImageName("dtzar/helm-kubectl");
		cli.setRepository("https://storage.googleapis.com/kubernetes-release/release/");
		cli.setDockerized(true);
		return cli;
	}

	@Override
	protected String getBinName() {
		return "kubectl";
	}

	@Override
	public ExecResult exec(KubectlExecSpec execSpec) {
		return super.exec(execSpec);
	}

	public ExecResult exec(Closure<KubectlExecSpec> closure) {
		KubectlExecSpec spec = new KubectlExecSpec();
		project.configure(spec, closure);
		return exec(spec);
	}
}