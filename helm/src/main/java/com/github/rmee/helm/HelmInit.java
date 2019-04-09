package com.github.rmee.helm;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;

public class HelmInit extends DefaultTask {

	private boolean skipRefresh;

	private String commandLine;

	public HelmInit() {
		setGroup("kubernetes");
	}

	@TaskAction
	public void exec() {
		if (commandLine == null) {
			commandLine = "helm init --client-only";
			if (skipRefresh) {
				commandLine += " --skip-refresh";
			}
		}
		HelmExtension extension = getProject().getExtensions().getByType(HelmExtension.class);
		HelmExecSpec spec = new HelmExecSpec();
		spec.setCommandLine(commandLine);
		extension.exec(spec);
	}

	public String getCommandLine() {
		return commandLine;
	}

	public void setCommandLine(String commandLine) {
		this.commandLine = commandLine;
	}

	public boolean isSkipRefresh() {
		return skipRefresh;
	}

	public void setSkipRefresh(boolean skipRefresh) {
		this.skipRefresh = skipRefresh;
	}
}
