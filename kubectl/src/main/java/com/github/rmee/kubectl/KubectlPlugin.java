package com.github.rmee.kubectl;

import java.io.File;
import java.net.MalformedURLException;

import com.github.rmee.common.Client;
import com.github.rmee.common.internal.KubernetesUtils;
import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class KubectlPlugin implements Plugin<Project> {


	public void apply(Project project) {
		project.getPlugins().apply("de.undercouch.download");

		KubectlExtension extension = project.getExtensions().create("kubectl", KubectlExtension.class);
		extension.setProject(project);
		extension.setNamespace("default");
		extension.setKubeConfig(KubernetesUtils.getDefaultKubeConfig(project));

		KubectlBootstrap bootstrap = project.getTasks().create("kubectlBootstrap", KubectlBootstrap.class);
		KubectlUseContext login = project.getTasks().create("kubectlUseContext", KubectlUseContext.class);
		project.getTasks().create("kubectlStartProxy", KubectlStartProxyTask.class);
		project.getTasks().create("kubectlClean", KubectlCleanTask.class);
		login.dependsOn(bootstrap);

		project.afterEvaluate(project1 -> {
			Client client = extension.getClient();
			if (client.isDockerized()) {
				bootstrap.setEnabled(false);
				client.setupWrapper(project);
				KubernetesUtils.addDefaultMappings(client, project);

				client.getOutputPaths().add(KubernetesUtils.KUBE_DIR);
			}
			else if (client.getDownload()) {
				File downloadDir = client.getDownloadDir();
				downloadDir.mkdirs();
				bootstrap.dest(downloadDir);
				try {
					bootstrap.src(client.getDownloadUrl());
				}
				catch (MalformedURLException e) {
					throw new IllegalStateException(e);
				}
			}
			else {
				bootstrap.setEnabled(false);
			}
		});
	}
}




