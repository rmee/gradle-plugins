package com.github.rmee.az.aks;

import java.io.File;

import com.github.rmee.az.AzExec;
import com.github.rmee.az.AzExtension;
import org.gradle.api.tasks.TaskAction;

/**
 * See https://docs.microsoft.com/en-us/azure/aks/kubernetes-walkthrough
 */
public class AzGetKubernetesCredentialsTask extends AzExec {

	@TaskAction
	public void run() {
		// az aks get-credentials --resource-group myResourceGroup --name myAKSClusterö
		AzExtension azExtension = getProject().getExtensions().getByType(AzExtension.class);
		String resourceGroup = azExtension.getResourceGroup();
		Object clusterName = azExtension.getAks().getClusterName();

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
