package com.github.rmee.kubernetes.oc;

import java.net.MalformedURLException;

import com.github.rmee.kubernetes.common.Client;
import com.github.rmee.kubernetes.common.internal.KubernetesUtils;
import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class OcPlugin implements Plugin<Project> {

	public void apply(Project project) {
		project.getPlugins().apply("de.undercouch.download");

		OcExtension extension = project.getExtensions().create("oc", OcExtension.class);
		extension.setProject(project);
		extension.setKubeConfig(KubernetesUtils.getDefaultKubeConfig(project));

		OcBootstrap ocBootstrap = project.getTasks().create("ocBootstrap", OcBootstrap.class);
		OcLogin ocLogin = project.getTasks().create("ocLogin", OcLogin.class);
		OcSetProject ocSetProject = project.getTasks().create("ocSetProject", OcSetProject.class);
		OcNewProject ocNewProject = project.getTasks().create("ocNewProject", OcNewProject.class);

		ocLogin.dependsOn(ocBootstrap);
		ocNewProject.dependsOn(ocLogin);
		ocSetProject.dependsOn(ocLogin);
		ocSetProject.mustRunAfter(ocNewProject);

		project.afterEvaluate(project1 -> {
			Client client = extension.getClient();
			if (client.isDockerized()) {
				ocBootstrap.setEnabled(false);
			}
			ocBootstrap.dest(client.getDownloadDir());
			try {
				ocBootstrap.src(client.getDownloadUrl());
			} catch (MalformedURLException e) {
				throw new IllegalStateException(e);
			}
		});
	}
}




