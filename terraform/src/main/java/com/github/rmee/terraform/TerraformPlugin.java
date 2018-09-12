package com.github.rmee.terraform;

import java.io.File;

import com.github.rmee.common.Client;
import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class TerraformPlugin implements Plugin<Project> {

	public void apply(Project project) {
		File configDir = new File(project.getProjectDir(), "src/main/terraform");
		TerraformExtension extension = project.getExtensions().create("terraform", TerraformExtension.class);
		extension.setProject(project);
		extension.setSourceDir(configDir);
		extension.getClient().setImageName("hashicorp/terraform");
		extension.getClient().setVersion("0.11.7");

		TerraformInitTask initTask = project.getTasks().create("terraformInit", TerraformInitTask.class);
		TerraformValidateTask validateTask = project.getTasks().create("terraformValidate", TerraformValidateTask.class);
		TerraformRefreshTask refreshTask = project.getTasks().create("terraformRefresh", TerraformRefreshTask.class);
		TerraformPlanTask planTask = project.getTasks().create("terraformPlan", TerraformPlanTask.class);
		TerraformApplyTask applyTask = project.getTasks().create("terraformApply", TerraformApplyTask.class);
		TerraformDestroyTask destroyTask = project.getTasks().create("terraformDestroy", TerraformDestroyTask.class);
		project.getTasks().create("terraformClean", TerraformCleanTask.class);

		refreshTask.dependsOn(initTask);
		validateTask.dependsOn(initTask);
		planTask.dependsOn(validateTask);
		applyTask.dependsOn(planTask);
		destroyTask.dependsOn(planTask);

		project.afterEvaluate(project1 -> {
			Client client = extension.getClient();
			client.setupWrapper(project, false);

			File terraformTempDir = new File(project.getBuildDir(), ".terraform.d");
			File terraformTempDir2 = new File(project.getBuildDir(), ".terraform");
			client.getVolumeMappings().put("/etc/project/conf", extension.getSourceDir());
			client.getVolumeMappings().put("/home/.terraform.d", terraformTempDir);
			client.getVolumeMappings().put("/.terraform", terraformTempDir2);

			client.getOutputPaths().add("/home/.terraform.d");
			client.getOutputPaths().add("/.terraform");

			if (extension.getDebug()) {
				client.getEnvironment().put("TF_LOG", "DEBUG");
			}
		});
	}
}




