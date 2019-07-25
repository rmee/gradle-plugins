package com.github.rmee.terraform;

import com.github.rmee.cli.base.Cli;
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
        extension.getCli().setVersion("0.11.7");

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
            Cli cli = extension.getCli();
            cli.setupWrapper(project, false);

            cli.addDefaultMappings(project);

            File terraformTempDir2 = new File(project.getBuildDir(), ".terraform");
            cli.getVolumeMappings().put("/etc/project/conf", extension.getSourceDir());
            cli.getVolumeMappings().put("/.terraform", terraformTempDir2);

            if (extension.getDebug()) {
                cli.getEnvironment().put("TF_LOG", "DEBUG");
            }
        });
    }
}




