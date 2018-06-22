package com.github.rmee.kubernetes.common.internal;

import com.github.rmee.kubernetes.common.Client;
import org.gradle.api.Project;

import java.io.File;

public class KubernetesUtils {
	public static File getDefaultKubeConfig(Project project) {
		return new File(project.getRootProject().getProjectDir(), "build/.kube/config");
	}

	public static void setKubeConfig(Client client, File kubeConfig) {
		if (client.isDockerized()) {
			client.getVolumeMappings().put("/root/.kube/", kubeConfig.getAbsolutePath());

		} else {
			client.getEnvironment().put("KUBECONFIG", kubeConfig.getAbsolutePath());
		}
	}
}
