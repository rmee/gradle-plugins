package com.github.rmee.oc;

import com.github.rmee.cli.base.Cli;
import com.github.rmee.cli.base.CliExecExtension;
import com.github.rmee.cli.base.CliExecPlugin;
import com.github.rmee.cli.base.Credentials;
import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class OcPlugin implements Plugin<Project> {

	protected static final String CONTAINER_SOURCES_DIR = "/src";

	public void apply(Project project) {
		project.getPlugins().apply(CliExecPlugin.class);

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

		CliExecExtension cliExec = project.getExtensions().getByType(CliExecExtension.class);
		cliExec.register("oc", extension.getCli());

		project.afterEvaluate(project1 -> {
			Cli cli = extension.getCli();
			if (cli.isDockerized()) {
				ocBootstrap.setEnabled(false);
			} else if (cli.getDownload()) {
				ocBootstrap.dest(cli.getDownloadDir());
				ocBootstrap.src(cli.getDownloadUrl());
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




