package com.github.rmee.jpa.schemagen;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.plugins.JavaPluginConvention;
import org.gradle.api.tasks.SourceSet;

class SchemaGenPlugin implements Plugin<Project> {

	@Override
	public void apply(Project project) {
		GenerateSchemaTask generateSchemaTask = project.getTasks().create(GenerateSchemaTask.NAME, GenerateSchemaTask.class);
		JavaPluginConvention javaConvention = project.getConvention().getPlugin(JavaPluginConvention.class);
		SourceSet mainSourceSet = javaConvention.getSourceSets().getByName(SourceSet.MAIN_SOURCE_SET_NAME);

		generateSchemaTask.setClasses(mainSourceSet.getOutput().getClassesDirs());


		SchemaGenExtension extension = new SchemaGenExtension();
		extension.project = project;

		project.getExtensions().add("jpaSchemaGen", extension);

		project.afterEvaluate(p -> {
			// jar dependency needs to be deferred as its depends on the plugin extension configuration
			if (extension.getContinuousMode()) {
				Task processResources = project.getTasks().getByName("processResources");
				processResources.finalizedBy(generateSchemaTask);
			}

			// dependant configuration is deffered as it is set be the user through the extension
			Configuration schemaGenConfig = project.getConfigurations().getByName(extension.getConfiguration());
			generateSchemaTask.dependsOn(schemaGenConfig);
			generateSchemaTask.setDependencies(schemaGenConfig);
		});
	}

}
