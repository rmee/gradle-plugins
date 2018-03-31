package com.github.rmee.kubernetes.helm;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;

public class HelmInit extends DefaultTask {

	public HelmInit() {
		setGroup("kubernetes");
	}

	@TaskAction
	public void exec() {
		HelmExtension extension = getExtension();
		HelmExecSpec spec = new HelmExecSpec();
		spec.setCommandLine("helm init --client-only");
		extension.exec(spec);
	}

	public HelmExtension getExtension() {
		return getProject().getExtensions().getByType(HelmExtension.class);
	}
}
