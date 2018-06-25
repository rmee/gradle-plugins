package com.github.rmee.common.internal;

import java.io.File;

import com.github.rmee.common.Client;
import org.gradle.api.Project;

public class KubernetesUtils {

	public static File getDefaultKubeConfig(Project project) {
		return new File(project.getRootProject().getProjectDir(), "build/.kube/config");
	}

	public static void setKubeConfig(Client client, File kubeConfig) {
		if (client.isDockerized()) {
			if (!kubeConfig.getName().equals("config")) {
				throw new IllegalStateException("kubeConfig must be named 'config', got " + kubeConfig.getAbsolutePath());
			}
			client.getVolumeMappings().put("/root/.kube/", kubeConfig.getParentFile());

		}
		else {
			client.getEnvironment().put("KUBECONFIG", kubeConfig.getAbsolutePath());
		}
	}

	public static void addDefaultMappings(Client client, Project project) {
		client.getVolumeMappings().put("/src", project.file("src"));
		client.getVolumeMappings().put("/build", project.file("build"));
	}
}
