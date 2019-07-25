package com.github.rmee.az.aks;

import com.github.rmee.az.AzExec;
import com.github.rmee.az.AzExtension;
import org.gradle.api.tasks.TaskAction;

import java.io.File;

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
        File file = azExtension.getCli().getHome(".kube/config");
        commandLine(
                String.format("az aks get-credentials --resource-group %s --name %s --file " + file,
                        resourceGroup, clusterName)
        );
        super.run();
    }
}
