package com.github.rmee.kubernetes.kubectl;

import java.io.File;
import java.net.MalformedURLException;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class KubectlPlugin implements Plugin<Project> {

	public void apply(Project project) {
		project.getPlugins().apply("de.undercouch.download");

		KubectlExtension extension = project.getExtensions().create("kubectl", KubectlExtension.class);
		extension.setProject(project);
		extension.setNamespace("default");

		KubectlBootstrap bootstrap = project.getTasks().create("kubectlBootstrap", KubectlBootstrap.class);
		KubectlUseContext login = project.getTasks().create("kubectlUseContext", KubectlUseContext.class);
		login.dependsOn(bootstrap);

		project.afterEvaluate(project1 -> {
			File downloadDir = extension.getClient().getDownloadDir();
			downloadDir.mkdirs();
			System.out.println(downloadDir);
			bootstrap.dest(downloadDir);
			try {
				bootstrap.src(extension.getClient().getDownloadUrl());
			}
			catch (MalformedURLException e) {
				throw new IllegalStateException(e);
			}
		});
	}
}




