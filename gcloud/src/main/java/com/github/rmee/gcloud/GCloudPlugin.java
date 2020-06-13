package com.github.rmee.gcloud;

import com.github.rmee.cli.base.Cli;
import com.github.rmee.cli.base.CliExecExtension;
import com.github.rmee.cli.base.ClientExtensionBase;
import com.github.rmee.gcloud.gke.GCloudGetKubernetesCredentialsTask;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.UnknownDomainObjectException;

import java.io.File;

public class GCloudPlugin implements Plugin<Project> {

	public void apply(Project project) {
		GCloudExtension extension = project.getExtensions().create("gcloud", GCloudExtension.class);
		extension.initProject(project);
		extension.getCli().getBinNames().add("gsutil");
		extension.getCli().setImageName("google/cloud-sdk");
		extension.getCli().setImageTag("159.0.0");

		GCloudActivateServiceAccountTask activateServiceAccount =
				project.getTasks().create("gcloudActivateServiceAccount", GCloudActivateServiceAccountTask.class);
		GCloudGetKubernetesCredentialsTask getCredentials =
				project.getTasks().create("gcloudGetKubernetesCredentials", GCloudGetKubernetesCredentialsTask.class);
		GCloudSetProjectTask setProject = project.getTasks().create("gcloudSetProject",
				GCloudSetProjectTask.class);

		getCredentials.dependsOn(setProject);


		extension.getGke().setKubeDir(new File(project.getRootProject().getProjectDir(), "build/.kube"));

		CliExecExtension cliExec = project.getExtensions().getByType(CliExecExtension.class);
		cliExec.register("gcloud", extension.getCli());
		cliExec.register("gsutil", extension.getCli());

		project.afterEvaluate(project1 -> {
			Cli cli = extension.getCli();

			// integrate with Kubernetes if available
			try {
				ClientExtensionBase kubectl = (ClientExtensionBase) project.getExtensions().getByName("kubectl");
				Cli kubectlCli = kubectl.getCli();
				kubectlCli.setImageName(cli.getImageName());
				kubectlCli.setImageTag(cli.getImageTag());
			} catch (UnknownDomainObjectException e) {
			}
		});
	}
}




