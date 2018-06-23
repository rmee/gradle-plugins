package com.github.rmee.terraform;

import org.gradle.api.Project;
import org.gradle.api.tasks.TaskAction;

import java.io.File;

public class TerraformPlanTask extends TerraformExec {

	public static final String CONTAINER_PLAN_FILE = CONTAINER_WORKING_DIRECTORY + "/plan";

	@TaskAction
	public void exec() {
		Project project = getProject();
		File planDir = project.file("build/terraform/plan");
		planDir.getParentFile().mkdirs();
		setCommandLine("plan -out=" + CONTAINER_PLAN_FILE);
		super.exec();
	}
}
