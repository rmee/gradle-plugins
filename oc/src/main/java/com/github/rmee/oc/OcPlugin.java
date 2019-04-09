package com.github.rmee.oc;

import com.github.rmee.common.Client;
import com.github.rmee.common.Credentials;
import com.github.rmee.common.internal.KubernetesUtils;
import org.gradle.api.Plugin;
import org.gradle.api.Project;

import java.net.MalformedURLException;

public class OcPlugin implements Plugin<Project> {

	protected static final String CONTAINER_SOURCES_DIR = "/src";

	public void apply(Project project) {
		project.getPlugins().apply("de.undercouch.download");

		OcExtension extension = project.getExtensions().create("oc", OcExtension.class);
		extension.setProject(project);
		// extension.setKubeConfig(KubernetesUtils.getDefaultKubeConfig(project));
		extension.setUrl(getVariable(project, "OPENSHIFT_URL"));
		Credentials credentials = extension.getCredentialsWithoutInit();
		credentials.setUserName(getVariable(project, "OPENSHIFT_USER"));
		credentials.setPassword(getVariable(project, "OPENSHIFT_PASS"));
		credentials.setToken(getVariable(project, "OPENSHIFT_TOKEN"));

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
				client.setupWrapper(project);
				KubernetesUtils.addDefaultMappings(client, project);
			} else if (client.getDownload()) {
				ocBootstrap.dest(client.getDownloadDir());
				try {
					ocBootstrap.src(client.getDownloadUrl());
				} catch (MalformedURLException e) {
					throw new IllegalStateException(e);
				}
			} else {
				ocBootstrap.setEnabled(false);
			}
		});
	}

	private String getVariable(Project project, String key) {
		String value = System.getenv(key);
		return value != null ? value : (String) project.getProperties().get(key);
	}
}




