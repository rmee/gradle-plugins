package com.github.rmee.az.aks;

import com.github.rmee.az.AzExec;
import com.github.rmee.az.AzExtension;
import org.gradle.api.tasks.TaskAction;

/**
 * See https://docs.microsoft.com/en-us/azure/aks/kubernetes-walkthrough
 */
public class AzKubernetesDashboardTask extends AzExec {

	@TaskAction
	public void run() {
		AzExtension azExtension = getProject().getExtensions().getByType(AzExtension.class);
		String resourceGroup = azExtension.getResourceGroup();
		Object clusterName = resourceGroup;
		setCommandLine(
				String.format("az aks browse --resource-group %s --name %s",
						resourceGroup, clusterName)
		);
		super.run();
	}
}
