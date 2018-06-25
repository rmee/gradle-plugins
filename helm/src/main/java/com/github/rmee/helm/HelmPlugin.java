package com.github.rmee.helm;

import java.io.File;
import java.net.MalformedURLException;
import java.util.Set;

import com.github.rmee.common.Client;
import com.github.rmee.common.internal.KubernetesUtils;
import org.gradle.api.DefaultTask;
import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class HelmPlugin implements Plugin<Project> {

	protected static final String CONTAINER_SOURCES_DIR = "/etc/project/sources";

	protected static final String CONTAINER_DISTRIBUTIONS_DIR = "/etc/project/distributions";

	public void apply(Project project) {
		project.getPlugins().apply("de.undercouch.download");

		HelmExtension extension = project.getExtensions().create("helm", HelmExtension.class);
		extension.setProject(project);
		extension.setKubeConfig(KubernetesUtils.getDefaultKubeConfig(project));

		HelmBootstrap helmBootstrap = project.getTasks().create("helmBootstrap", HelmBootstrap.class);
		DefaultTask helmPackages = project.getTasks().create("helmPackage", DefaultTask.class);
		helmPackages.setGroup("kubernetes");
		HelmInit helmInit = project.getTasks().create("helmInit", HelmInit.class);

		Set<String> packageNames = extension.getPackageNames();
		for (String packageName : packageNames) {
			HelmPackage helmPackage = project.getTasks().create("helmPackage" + toCamelCase(packageName), HelmPackage.class);
			helmPackage.setPackageName(packageName);
			helmPackage.dependsOn(helmBootstrap);
			helmPackages.dependsOn(helmPackage);
		}

		project.afterEvaluate(project1 -> {
			Client client = extension.getClient();


			if (client.isDockerized()) {
				helmBootstrap.setEnabled(false);
				client.setupWrapper(project);

				File helmTempDir = new File(project.getBuildDir(), ".helm");
				File helmDistDir = new File(project.getBuildDir(), "distributions");
				extension.getClient().getVolumeMappings().put(CONTAINER_SOURCES_DIR, extension.getSourceDir());
				extension.getClient().getVolumeMappings().put(CONTAINER_DISTRIBUTIONS_DIR, helmDistDir);
				extension.getClient().getVolumeMappings().put("/root/.helm/", helmTempDir);
			}
			else if (client.getDownload()) {
				try {
					helmBootstrap.src(client.getDownloadUrl());
				}
				catch (MalformedURLException e) {
					throw new IllegalStateException(e);
				}
				File downloadDir = client.getDownloadDir();
				downloadDir.mkdirs();
				helmBootstrap.dest(downloadDir);
			}
		});

		helmInit.dependsOn(helmBootstrap);
	}

	private String toCamelCase(String packageName) {
		StringBuilder builder = new StringBuilder();
		char[] chars = packageName.toCharArray();
		for (int i = 0; i < chars.length; i++) {
			char c = chars[i];
			if (i == 0 || packageName.charAt(i - 1) == '-') {
				builder.append(Character.toUpperCase(c));
			}
			else if (c != '-') {
				builder.append(c);
			}
		}
		return builder.toString();
	}
}

