package com.github.rmee.terraform;

import com.github.rmee.common.Client;
import com.github.rmee.common.internal.KubernetesUtils;
import org.gradle.api.Plugin;
import org.gradle.api.Project;

import java.io.File;

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

        refreshTask.dependsOn(initTask);
        validateTask.dependsOn(initTask);
        planTask.dependsOn(validateTask);
        applyTask.dependsOn(planTask);
        destroyTask.dependsOn(planTask);

        project.afterEvaluate(project1 -> {
            Client client = extension.getClient();
            client.setupWrapper(project, false);

            KubernetesUtils.addDefaultMappings(client, project);

            File terraformTempDir2 = new File(project.getBuildDir(), ".terraform");
            client.getVolumeMappings().put("/etc/project/conf", extension.getSourceDir());
            client.getVolumeMappings().put("/.terraform", terraformTempDir2);

            if (extension.getDebug()) {
                client.getEnvironment().put("TF_LOG", "DEBUG");
            }
        });
    }
}




