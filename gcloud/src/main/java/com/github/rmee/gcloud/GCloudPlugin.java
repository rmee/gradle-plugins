package com.github.rmee.gcloud;

import java.io.File;

import com.github.rmee.common.Client;
import com.github.rmee.common.ClientExtensionBase;
import com.github.rmee.common.internal.KubernetesUtils;
import com.github.rmee.gcloud.gke.GCloudGetKubernetesCredentialsTask;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.UnknownDomainObjectException;

public class GCloudPlugin implements Plugin<Project> {

	public void apply(Project project) {
		File configDir = new File(project.getBuildDir(), ".gcloud");

		GCloudExtension extension = project.getExtensions().create("gcloud", GCloudExtension.class);
		extension.initProject(project);
		extension.getClient().setImageName("google/cloud-sdk");
		extension.getClient().setVersion("159.0.0");

		GCloudActivateServiceAccountTask activateServiceAccount =
				project.getTasks().create("gcloudActivateServiceAccount", GCloudActivateServiceAccountTask.class);
		GCloudGetKubernetesCredentialsTask getCredentials =
				project.getTasks().create("gcloudGetKubernetesCredentials", GCloudGetKubernetesCredentialsTask.class);
		GCloudSetProjectTask setProject = project.getTasks().create("gcloudSetProject",
				GCloudSetProjectTask.class);

		getCredentials.dependsOn(setProject);


		extension.getGke().setKubeDir(new File(project.getRootProject().getProjectDir(), "build/.kube"));

		project.afterEvaluate(project1 -> {
			Client client = extension.getClient();
			KubernetesUtils.addDefaultMappings(client, project);
			client.setupWrapper(project);

			// integrate with Kubernetes if available
			try {
				ClientExtensionBase kubectl = (ClientExtensionBase) project.getExtensions().getByName("kubectl");
				Client kubectlClient = kubectl.getClient();
				kubectlClient.setImageName(client.getImageName());
				kubectlClient.setVersion(client.getVersion());
			}catch(UnknownDomainObjectException e){
			}
		});
	}
}




