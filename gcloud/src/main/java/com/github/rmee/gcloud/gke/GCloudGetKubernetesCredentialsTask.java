package com.github.rmee.gcloud.gke;

import java.io.File;

import com.github.rmee.gcloud.GCloudExecSpec;
import com.github.rmee.gcloud.GCloudExtension;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.Optional;
import org.gradle.api.tasks.OutputFile;
import org.gradle.api.tasks.TaskAction;

/**
 */
public class GCloudGetKubernetesCredentialsTask extends DefaultTask {

	private GCloudExtension getExtension() {
		return getProject().getExtensions().getByType(GCloudExtension.class);
	}

	@Optional
	@Input
	public String getRegion() {
		return getExtension().getRegion();
	}

	@Input
	public String getZone() {
		return getExtension().getZone();
	}

	@Input
	public String getClusterName() {
		return getExtension().getGke().getClusterName();
	}

	@OutputFile
	public File getConfigFile() {
		return getProject().file("build/wrapper/.kube/config" );
	}

	@TaskAction
	public void run() {
		// az aks get-credentials --resource-group myResourceGroup --name myAKSClusterö
		StringBuilder builder = new StringBuilder();
		builder.append("gcloud container clusters get-credentials ");
		builder.append(getClusterName());

		String region = getRegion();
		if(region != null) {
			builder.append(" --region=");
			builder.append(region);
		}

		builder.append(" --zone=");
		builder.append(getZone());

		GCloudExecSpec execSpec = new GCloudExecSpec();
		execSpec.setCommandLine(builder.toString());
		//execSpec.setStdoutFile(tempFile);
		getExtension().exec(execSpec);



		/*String resourceGroup = GCloudExtension.getResourceGroup();
		Object clusterName = GCloudExtension.getGke().getClusterName();

		// replace contents
		File file = new File("/root/.kube/config");
		file.delete();

		commandLine(
				String.format("az aks get-credentials --resource-group %s --name %s --file /build/wrapper/.kube/config",
						resourceGroup, clusterName)
		);
		*/
	}
}
