package com.github.rmee.terraform;

import com.github.rmee.common.Client;
import org.gradle.api.Plugin;
import org.gradle.api.Project;

import java.io.File;

public class TerraformPlugin implements Plugin<Project> {

	public void apply(Project project) {
		File configDir = new File(project.getProjectDir(), "src/main/terraform");
		TerraformExtension extension = project.getExtensions().create("terraform", TerraformExtension.class);
		extension.setConfigDirectory(configDir);


		TerraformInitTask initTask = project.getTasks().create("terraformInit", TerraformInitTask.class);
		TerraformValidateTask validateTask = project.getTasks().create("terraformValidate", TerraformValidateTask.class);
		TerraformRefreshTask refreshTask = project.getTasks().create("terraformRefresh", TerraformRefreshTask.class);
		TerraformPlanTask planTask = project.getTasks().create("terraformPlan", TerraformPlanTask.class);
		TerraformApplyTask applyTask = project.getTasks().create("terraformApply", TerraformApplyTask.class);
		TerraformDestroyTask destroyTask = project.getTasks().create("terraformDestroy", TerraformDestroyTask.class);

		refreshTask.dependsOn(initTask);
		validateTask.dependsOn(initTask);
		planTask.dependsOn(validateTask);
		applyTask.dependsOn(planTask);
		destroyTask.dependsOn(planTask);

		project.afterEvaluate(project1 -> {
			Client client = extension.getClient();
			if (client.isDockerized()) {
				client.setupWrapper(project);
			}
		});
	}
}




