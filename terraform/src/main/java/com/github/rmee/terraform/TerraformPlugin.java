package com.github.rmee.terraform;

import com.github.rmee.cli.base.Cli;
import com.github.rmee.cli.base.CliExecExtension;
import com.github.rmee.cli.base.CliExecPlugin;
import org.gradle.api.Plugin;
import org.gradle.api.Project;

import java.io.File;

public class TerraformPlugin implements Plugin<Project> {

	public void apply(Project project) {
		project.getPlugins().apply(CliExecPlugin.class);

		File configDir = new File(project.getProjectDir(), "src/main/terraform");
		TerraformExtension extension = project.getExtensions().create("terraform", TerraformExtension.class);
		extension.setProject(project);
		extension.setSourceDir(configDir);
		extension.getCli().setImageName("hashicorp/terraform");
		extension.getCli().setImageTag("0.11.7");

		// terraform needs to have its files in the local working directory
		extension.getCli().setWorkingDir("/workdir/src/main/terraform");

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

		CliExecExtension cliExec = project.getExtensions().getByType(CliExecExtension.class);
		cliExec.register("terraform", extension.getCli());

		project.afterEvaluate(project1 -> {
			Cli cli = extension.getCli();

			File terraformTempDir2 = new File(project.getBuildDir(), ".terraformCache");
			cli.getVolumeMappings().put("/.terraform", terraformTempDir2);

			if (extension.getDebug()) {
				cli.getEnvironment().put("TF_LOG", "DEBUG");
			}
		});
	}
}




