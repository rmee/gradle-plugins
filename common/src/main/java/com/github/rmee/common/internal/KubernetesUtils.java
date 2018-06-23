package com.github.rmee.common.internal;

import com.github.rmee.common.Client;
import org.gradle.api.Project;

import java.io.File;

public class KubernetesUtils {
	public static File getDefaultKubeConfig(Project project) {
		return new File(project.getRootProject().getProjectDir(), "build/.kube/config");
	}

	public static void setKubeConfig(Client client, File kubeConfig) {
		if (client.isDockerized()) {
			if (!kubeConfig.getName().equals("config")) {
				throw new IllegalStateException("kubeConfig must be named 'config', got " + kubeConfig.getAbsolutePath());
			}
			client.getVolumeMappings().put("/root/.kube/", kubeConfig.getParentFile().getAbsolutePath());

		} else {
			client.getEnvironment().put("KUBECONFIG", kubeConfig.getAbsolutePath());
		}
	}
}
