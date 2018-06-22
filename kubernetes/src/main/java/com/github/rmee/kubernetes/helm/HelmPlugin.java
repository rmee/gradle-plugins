package com.github.rmee.kubernetes.helm;

import java.io.File;
import java.net.MalformedURLException;

import com.github.rmee.kubernetes.common.Client;
import com.github.rmee.kubernetes.common.internal.KubernetesUtils;
import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class HelmPlugin implements Plugin<Project> {

	public void apply(Project project) {
		project.getPlugins().apply("de.undercouch.download");

		HelmExtension extension = project.getExtensions().create("helm", HelmExtension.class);
		extension.setProject(project);
		extension.setKubeConfig(KubernetesUtils.getDefaultKubeConfig(project));

		HelmBootstrap helmBootstrap = project.getTasks().create("helmBootstrap", HelmBootstrap.class);
		HelmPackage helmPackage = project.getTasks().create("helmPackage", HelmPackage.class);
		HelmInit helmInit = project.getTasks().create("helmInit", HelmInit.class);

		project.afterEvaluate(project1 -> {
			Client client = extension.getClient();
			if (client.isDockerized()) {
				helmBootstrap.setEnabled(false);
			} else if (client.getDownload()) {
				try {
					helmBootstrap.src(client.getDownloadUrl());
				} catch (MalformedURLException e) {
					throw new IllegalStateException(e);
				}
				File downloadDir = client.getDownloadDir();
				downloadDir.mkdirs();
				helmBootstrap.dest(downloadDir);
			}
		});

		helmPackage.dependsOn(helmBootstrap);
		helmInit.dependsOn(helmBootstrap);
	}
}

