package com.github.rmee.kubectl;

import com.github.rmee.cli.base.Cli;
import com.github.rmee.cli.base.CliExecExtension;
import com.github.rmee.cli.base.CliExecPlugin;
import org.gradle.api.Plugin;
import org.gradle.api.Project;

import java.io.File;

public class KubectlPlugin implements Plugin<Project> {


	public void apply(Project project) {
		project.getPlugins().apply(CliExecPlugin.class);

		KubectlExtension extension = project.getExtensions().create("kubectl", KubectlExtension.class);
		extension.setProject(project);
		extension.setNamespace("default");

		KubectlBootstrap bootstrap = project.getTasks().create("kubectlBootstrap", KubectlBootstrap.class);
		KubectlUseContext login = project.getTasks().create("kubectlUseContext", KubectlUseContext.class);
		project.getTasks().create("kubectlStartProxy", KubectlStartProxyTask.class);
		login.dependsOn(bootstrap);

		CliExecExtension cliExec = project.getExtensions().getByType(CliExecExtension.class);
		cliExec.register("kubectl", extension.getCli());

		project.afterEvaluate(project1 -> {
			Cli cli = extension.getCli();
			if (cli.isDockerized()) {
				bootstrap.setEnabled(false);
			} else if (cli.getDownload()) {
				File downloadDir = cli.getDownloadDir();
				downloadDir.mkdirs();
				bootstrap.dest(downloadDir);
				bootstrap.src(cli.getDownloadUrl());
			} else {
				bootstrap.setEnabled(false);
			}
		});
	}
}




