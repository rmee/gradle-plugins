package com.github.rmee.gcloud.gke;

import com.github.rmee.gcloud.GCloudExecSpec;
import com.github.rmee.gcloud.GCloudExtension;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;

/**
 */
public class GCloudGetKubernetesCredentialsTask extends DefaultTask {

	@TaskAction
	public void run() {
		// az aks get-credentials --resource-group myResourceGroup --name myAKSClusterö
		GCloudExtension extension = getProject().getExtensions().getByType(GCloudExtension.class);

		String region = extension.getRegion();
		String zone = extension.getZone();
		String clusterName = extension.getGke().getClusterName();
		if(zone == null){
			throw new IllegalStateException("gcloud.zone not configured");
		}

		StringBuilder builder = new StringBuilder();
		builder.append("gcloud container clusters get-credentials ");
		builder.append(clusterName);




		if(region != null) {
			builder.append(" --region=");
			builder.append(region);
		}

		builder.append(" --zone=");
		builder.append(zone);

		GCloudExecSpec execSpec = new GCloudExecSpec();
		execSpec.setCommandLine(builder.toString());
		//execSpec.setStdoutFile(tempFile);
		extension.exec(execSpec);



		/*String resourceGroup = GCloudExtension.getResourceGroup();
		Object clusterName = GCloudExtension.getGke().getClusterName();

		// replace contents
		File file = new File("/root/.kube/config");
		file.delete();

		commandLine(
				String.format("az aks get-credentials --resource-group %s --name %s --file /home/.kube/config",
						resourceGroup, clusterName)
		);
		*/
	}
}
