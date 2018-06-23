package com.github.rmee.kubernetes.kubectl;

import java.io.File;
import java.net.MalformedURLException;

import com.github.rmee.kubernetes.common.Client;
import com.github.rmee.kubernetes.common.internal.KubernetesUtils;
import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;

public class KubectlPlugin implements Plugin<Project> {

	public void apply(Project project) {
		project.getPlugins().apply("de.undercouch.download");

		KubectlExtension extension = project.getExtensions().create("kubectl", KubectlExtension.class);
		extension.setProject(project);
		extension.setNamespace("default");
		extension.setKubeConfig(KubernetesUtils.getDefaultKubeConfig(project));

		KubectlBootstrap bootstrap = project.getTasks().create("kubectlBootstrap", KubectlBootstrap.class);
		KubectlUseContext login = project.getTasks().create("kubectlUseContext", KubectlUseContext.class);
		login.dependsOn(bootstrap);

		project.afterEvaluate(project1 -> {
			Client client = extension.getClient();
			if (client.isDockerized()) {
				bootstrap.setEnabled(false);
				client.setupWrapper(project);
			} else {
				File downloadDir = client.getDownloadDir();
				downloadDir.mkdirs();
				bootstrap.dest(downloadDir);
				try {
					bootstrap.src(client.getDownloadUrl());
				} catch (MalformedURLException e) {
					throw new IllegalStateException(e);
				}
			}
		});
	}
}




