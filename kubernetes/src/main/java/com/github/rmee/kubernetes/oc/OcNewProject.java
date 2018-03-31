package com.github.rmee.kubernetes.oc;

import org.gradle.api.tasks.TaskAction;

public class OcNewProject extends OcExec {

	public OcNewProject() {
		setIgnoreExitValue(true);
	}

	@TaskAction
	protected void exec() {
		OcExtension extension = getProject().getExtensions().getByType(OcExtension.class);
		setCommandLine("oc new-project " + extension.getProjectName());
		super.exec();
	}


}
