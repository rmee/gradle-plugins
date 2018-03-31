package com.github.rmee.kubernetes.helm;

import java.io.File;
import java.net.MalformedURLException;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class HelmPlugin implements Plugin<Project> {

	public void apply(Project project) {
		project.getPlugins().apply("de.undercouch.download");

		HelmExtension extension = project.getExtensions().create("helm", HelmExtension.class);
		extension.setProject(project);

		HelmBootstrap helmBootstrap = project.getTasks().create("helmBootstrap", HelmBootstrap.class);
		HelmPackage helmPackage = project.getTasks().create("helmPackage", HelmPackage.class);
		HelmInit helmInit = project.getTasks().create("helmInit", HelmInit.class);

		project.afterEvaluate(project1 -> {
			if (extension.getClient().getDownload()) {
				try {
					helmBootstrap.src(extension.getClient().getDownloadUrl());
				}
				catch (MalformedURLException e) {
					throw new IllegalStateException(e);
				}
				File downloadDir = extension.getClient().getDownloadDir();
				downloadDir.mkdirs();
				helmBootstrap.dest(downloadDir);
			}
		});

		helmPackage.dependsOn(helmBootstrap);
		helmInit.dependsOn(helmBootstrap);
	}
}

