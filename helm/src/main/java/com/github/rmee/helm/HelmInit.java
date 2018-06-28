package com.github.rmee.helm;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;

public class HelmInit extends DefaultTask {

	public HelmInit() {
		setGroup("kubernetes");
	}

	@TaskAction
	public void exec() {
		HelmExtension extension = getProject().getExtensions().getByType(HelmExtension.class);
		HelmExecSpec spec = new HelmExecSpec();
		spec.setCommandLine("helm init --client-only");
		extension.exec(spec);
	}
}
