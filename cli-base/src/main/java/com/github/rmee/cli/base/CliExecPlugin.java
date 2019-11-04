package com.github.rmee.cli.base;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class CliExecPlugin implements Plugin<Project> {

	public void apply(Project project) {
		project.getPlugins().apply("de.undercouch.download");
		project.getExtensions().create("cliExec", CliExecExtension.class);
	}
}

