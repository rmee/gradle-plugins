package com.github.rmee.jdk.bootstrap;


import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;

public class JdkBootstrapPlugin implements Plugin<Project> {

	@Override
	public void apply(Project project) {
		JdkBootstrapExtension extension = project.getExtensions().create("jdk", JdkBootstrapExtension.class);
		Task wrapperTask = project.getTasks().getByName("wrapper");
		wrapperTask.doLast(new GenerateBootstrapScript(extension));
	}
}
