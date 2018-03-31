package com.github.rmee.kubernetes.helm;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;

public class HelmExec extends DefaultTask {

	private HelmExecSpec spec = new HelmExecSpec();

	public HelmExec() {
		setGroup("kubernetes");

		dependsOn("helmBootstrap");
	}

	@TaskAction
	public void exec() {
		HelmExtension extension = getExtension();
		extension.exec(spec);
	}

	public HelmExtension getExtension() {
		return getProject().getExtensions().getByType(HelmExtension.class);
	}

	public boolean isIgnoreExitValue() {
		return spec.isIgnoreExitValue();
	}

	public void setIgnoreExitValue(boolean ignoreExitValue) {
		spec.setIgnoreExitValue(ignoreExitValue);
	}

	public String getCommandLine() {
		return spec.getCommandLine();
	}

	public void setCommandLine(String commandLine) {
		spec.setCommandLine(commandLine);
	}


}
