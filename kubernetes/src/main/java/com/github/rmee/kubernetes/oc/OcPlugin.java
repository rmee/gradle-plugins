package com.github.rmee.kubernetes.oc;

import java.net.MalformedURLException;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class OcPlugin implements Plugin<Project> {

	public void apply(Project project) {
		project.getPlugins().apply("de.undercouch.download");

		OcExtension extension = project.getExtensions().create("oc", OcExtension.class);
		extension.setProject(project);

		OcBootstrap ocBootstrap = project.getTasks().create("ocBootstrap", OcBootstrap.class);
		OcLogin ocLogin = project.getTasks().create("ocLogin", OcLogin.class);
		OcSetProject ocSetProject = project.getTasks().create("ocSetProject", OcSetProject.class);
		OcNewProject ocNewProject = project.getTasks().create("ocNewProject", OcNewProject.class);

		ocLogin.dependsOn(ocBootstrap);
		ocNewProject.dependsOn(ocLogin);
		ocSetProject.dependsOn(ocLogin);

		project.afterEvaluate(project1 -> {
			ocBootstrap.dest(extension.getClient().getDownloadDir());
			try {
				ocBootstrap.src(extension.getClient().getDownloadUrl());
			}
			catch (MalformedURLException e) {
				throw new IllegalStateException(e);
			}
		});
	}
}




