package com.github.rmee.kubernetes.oc;

import org.gradle.api.tasks.TaskAction;

public class OcSetProject extends OcExec {

	public OcSetProject() {
		setIgnoreExitValue(true);
	}

	@TaskAction
	protected void exec() {
		OcExtension extension = getProject().getExtensions().getByType(OcExtension.class);
		setCommandLine("oc project " + extension.getProjectName());
		super.exec();
	}


}
