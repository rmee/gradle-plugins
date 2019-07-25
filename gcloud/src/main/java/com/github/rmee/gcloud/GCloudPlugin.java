package com.github.rmee.gcloud;

import com.github.rmee.cli.base.Cli;
import com.github.rmee.cli.base.ClientExtensionBase;
import com.github.rmee.gcloud.gke.GCloudGetKubernetesCredentialsTask;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.UnknownDomainObjectException;

import java.io.File;

public class GCloudPlugin implements Plugin<Project> {

    public void apply(Project project) {
        File configDir = new File(project.getBuildDir(), ".gcloud");

        GCloudExtension extension = project.getExtensions().create("gcloud", GCloudExtension.class);
        extension.initProject(project);
        extension.getCli().setImageName("google/cloud-sdk");
        extension.getCli().setVersion("159.0.0");

        GCloudActivateServiceAccountTask activateServiceAccount =
                project.getTasks().create("gcloudActivateServiceAccount", GCloudActivateServiceAccountTask.class);
        GCloudGetKubernetesCredentialsTask getCredentials =
                project.getTasks().create("gcloudGetKubernetesCredentials", GCloudGetKubernetesCredentialsTask.class);
        GCloudSetProjectTask setProject = project.getTasks().create("gcloudSetProject",
                GCloudSetProjectTask.class);

        getCredentials.dependsOn(setProject);


        extension.getGke().setKubeDir(new File(project.getRootProject().getProjectDir(), "build/.kube"));

        project.afterEvaluate(project1 -> {
            Cli cli = extension.getCli();
            cli.addDefaultMappings(project);
            cli.setupWrapper(project);

            // integrate with Kubernetes if available
            try {
                ClientExtensionBase kubectl = (ClientExtensionBase) project.getExtensions().getByName("kubectl");
                Cli kubectlCli = kubectl.getCli();
                kubectlCli.setImageName(cli.getImageName());
                kubectlCli.setVersion(cli.getVersion());
            } catch (UnknownDomainObjectException e) {
            }
        });
    }
}




