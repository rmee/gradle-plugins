package com.github.rmee.gcloud.aks;

import java.io.File;

import com.github.rmee.gcloud.GCloudExec;
import com.github.rmee.gcloud.GCloudExtension;
import org.gradle.api.tasks.TaskAction;

/**
 * See https://docs.microsoft.com/en-us/azure/aks/kubernetes-walkthrough
 */
public class GCloudGetKubernetesCredentialsTask extends GCloudExec {

	@TaskAction
	public void run() {
		// az aks get-credentials --resource-group myResourceGroup --name myAKSClusterö
		GCloudExtension GCloudExtension = getProject().getExtensions().getByType(GCloudExtension.class);
		String resourceGroup = GCloudExtension.getResourceGroup();
		Object clusterName = GCloudExtension.getAks().getClusterName();

		// replace contents
		File file = new File("/root/.kube/config");
		file.delete();

		commandLine(
				String.format("az aks get-credentials --resource-group %s --name %s --file /root/.kube/config",
						resourceGroup, clusterName)
		);
		super.run();
	}
}
