package com.github.rmee.helm;

import com.github.rmee.cli.base.Cli;
import com.github.rmee.cli.base.CliExecExtension;
import com.github.rmee.cli.base.CliExecPlugin;
import org.gradle.api.DefaultTask;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;

import java.io.File;
import java.util.Set;

public class HelmPlugin implements Plugin<Project> {

	protected static final String HELM_OUTPUT_DIR = "/workdir/build/helm";

	public void apply(Project project) {
		project.getPlugins().apply(CliExecPlugin.class);

		HelmExtension extension = project.getExtensions().create("helm", HelmExtension.class);
		extension.setProject(project);
		// extension.setKubeConfig(KubernetesUtils.getDefaultKubeConfig(project));

		HelmBootstrap helmBootstrap = project.getTasks().create("helmBootstrap", HelmBootstrap.class);

		DefaultTask helmPackages = project.getTasks().create("helmPackage", DefaultTask.class);
		HelmPublish helmPublish = project.getTasks().create("helmPublish", HelmPublish.class);
		Task helmUpdateRepository = project.getTasks().create("helmUpdateRepository", HelmUpdateRepository.class);
		DefaultTask helmPackagePrepare = project.getTasks().create("helmPackagePrepare", DefaultTask.class);
		helmPackagePrepare.dependsOn(helmBootstrap);

		helmUpdateRepository.dependsOn(helmBootstrap);
		helmPublish.dependsOn(helmUpdateRepository);
		helmPublish.dependsOn(helmPackages);

		helmPackages.setGroup("kubernetes");

		CliExecExtension cliExec = project.getExtensions().getByType(CliExecExtension.class);
		cliExec.register("helm", extension.getCli());

		Set<String> packageNames = extension.getPackageNames();
		for (String packageName : packageNames) {
			HelmPackage helmPackage = project.getTasks().create("helmPackage" + toCamelCase(packageName), HelmPackage.class);
			helmPackage.setPackageName(packageName);
			helmPackage.dependsOn(helmPackagePrepare);
			helmPackages.dependsOn(helmPackage);
		}

		project.afterEvaluate(project1 -> {
			Cli cli = extension.getCli();

			if (cli.isDockerized()) {
				helmBootstrap.setEnabled(false);
			} else if (cli.getDownload()) {
				helmBootstrap.src(cli.getDownloadUrl());
				File downloadDir = cli.getDownloadDir();
				downloadDir.mkdirs();
				helmBootstrap.dest(downloadDir);
			} else {
				helmBootstrap.setEnabled(false);
			}
		});
	}

	private String toCamelCase(String packageName) {
		StringBuilder builder = new StringBuilder();
		char[] chars = packageName.toCharArray();
		for (int i = 0; i < chars.length; i++) {
			char c = chars[i];
			if (i == 0 || packageName.charAt(i - 1) == '-') {
				builder.append(Character.toUpperCase(c));
			} else if (c != '-') {
				builder.append(c);
			}
		}
		return builder.toString();
	}
}

