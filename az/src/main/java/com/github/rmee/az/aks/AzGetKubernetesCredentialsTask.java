package com.github.rmee.az.aks;

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
		Object clusterName = resourceGroup;

		setCommand(
				String.format("az aks get-credentials --resource-group %s --name %s --file /root/.kube/config",
						resourceGroup, clusterName)
		);

		captureOutput = true;

		super.run();
	}
}
