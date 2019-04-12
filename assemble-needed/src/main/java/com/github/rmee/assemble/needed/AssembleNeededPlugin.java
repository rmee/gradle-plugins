package com.github.rmee.assemble.needed;

import org.gradle.api.DomainObjectSet;
import org.gradle.api.NonNullApi;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.artifacts.ProjectDependency;

/**
 * Implements assembleNeeded to build all needed (maybe transitive) project dependencies.
 */
@NonNullApi
public class AssembleNeededPlugin implements Plugin<Project> {

	@Override
	public void apply(Project project) {
		project.getPlugins().apply("base");

		Task assembleNeeded = project.getTasks().create("assembleNeeded");
		assembleNeeded.dependsOn("assemble");
		project.afterEvaluate(project1 -> {
			DomainObjectSet<ProjectDependency> projectDependencies = project1.getConfigurations().getByName("runtime").getAllDependencies().withType(ProjectDependency.class);
			for (ProjectDependency neededProjectDependency : projectDependencies) {
				assembleNeeded.dependsOn(neededProjectDependency.getDependencyProject().getPath() + ":assembleNeeded");
			}
		});
	}
}
