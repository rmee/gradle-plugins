package com.github.rmee.gcloud;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;

public class GCloudSetProjectTask extends DefaultTask {

	@TaskAction
	public void run() {
		GCloudExtension extension = getProject().getExtensions().getByType(GCloudExtension.class);

		String project = extension.getProject();
		if (project == null) {
			throw new IllegalStateException("gcloud.project not configured");
		}

		StringBuilder command = new StringBuilder();
		command.append("gcloud config set project ");
		command.append(project);

		GCloudExecSpec execSpec = new GCloudExecSpec();
		execSpec.setCommandLine(command.toString());
		extension.exec(execSpec);

	}
}
