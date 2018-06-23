package com.github.rmee.az;

import com.github.rmee.az.aks.AzGetKubernetesCredentialsTask;
import com.github.rmee.az.aks.AzKubernetesDashboardTask;
import com.github.rmee.common.Client;
import org.gradle.api.Plugin;
import org.gradle.api.Project;

import java.io.File;

public class AzPlugin implements Plugin<Project> {

	public void apply(Project project) {
		File azureConfigDir = new File(project.getBuildDir(), ".azure");

		AzExtension extension = project.getExtensions().create("az", AzExtension.class);
		extension.getClient().setImageName("microsoft/azure-cli");

		AzLoginTask login = project.getTasks().create("azLogin", AzLoginTask.class);
		AzKubernetesDashboardTask dashboard = project.getTasks().create("azKubernetesDashboard", AzKubernetesDashboardTask.class);
		AzGetKubernetesCredentialsTask getCredentials = project.getTasks().create("azGetKubernetesCredentials", AzGetKubernetesCredentialsTask.class);
		getCredentials.dependsOn(login);
		dashboard.dependsOn(getCredentials);

		extension.setSubscriptionId(System.getenv("AZ_SUBSCRIPTION_ID"));
		extension.setServicePrincipal(Boolean.parseBoolean(System.getenv("AZ_SERVICE_PRINCIPLE")));
		extension.setUserName(System.getenv("AZ_USER"));
		extension.setPassword(System.getenv("AZ_PASS"));
		extension.setTenantId(System.getenv("AZ_TENANT_ID"));
		extension.getAks().setKubeDir(new File(project.getRootProject().getProjectDir(), "build/.kube"));

		project.afterEvaluate(project1 -> {
			Client client = extension.getClient();
			client.getVolumeMappings().put(azureConfigDir.getAbsolutePath(), "/root/.azure/");
			client.getVolumeMappings().put(extension.getAks().getKubeDir().getAbsolutePath(), "/root/.kube/");
			client.setupWrapper(project);
		});
	}
}




