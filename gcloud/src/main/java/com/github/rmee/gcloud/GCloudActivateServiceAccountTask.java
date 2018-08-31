package com.github.rmee.gcloud;

import java.io.File;
import java.io.IOException;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;

public class GCloudActivateServiceAccountTask extends DefaultTask {

	@TaskAction
	public void run() {
		GCloudExtension extension = getProject().getExtensions().getByType(GCloudExtension.class);

		File keyFile = extension.getKeyFile();
		if (keyFile == null) {
			throw new IllegalStateException("gcloud.keyFile not configured");
		}

		StringBuilder command = new StringBuilder();
		command.append("gcloud auth activate-service-account");

		command.append(" --key-file ");
		command.append(keyFile);


		/*
		if (servicePrincipal) {
			command.append(" --service-principal");
		}
		if (userName != null) {
			command.append(" -u " + userName);
		}
		if (password != null) {
			command.append(" -p " + password);
		}
		if (tenantId != null) {
			command.append(" --tenant " + tenantId);
		}
		*/

		// 	command = new StringBuilder("gcloud info --format=json");


		File tempFile;
		try {
			tempFile = File.createTempFile("gcloud-activate-service-account", "txt");
		}
		catch (IOException e) {
			throw new IllegalStateException(e);
		}

		GCloudExecSpec execSpec = new GCloudExecSpec();
		//		execSpec.setVolumesFrom(null);
		//	execSpec.setContainerName(extension.getContainerName());

		execSpec.setCommandLine(command.toString());
		//execSpec.setStdoutFile(tempFile);
		extension.exec(execSpec);

/*
		if (GCloudExtension.getSubscriptionId() == null || GCloudExtension.getTenantId() == null) {
			List logins = (List) new JsonSlurper().parse(tempFile, "utf8");
			if (logins.size() != 1) {
				throw new IllegalStateException("only single subscription implemented, got " + logins);
			}
			Map subscription = (Map) logins.get(0);
			String subscriptionId = Objects.requireNonNull((String) subscription.get("id"));
			String subTenantId = Objects.requireNonNull((String) subscription.get("tenantId"));

			if (GCloudExtension.getTenantId() == null) {
				GCloudExtension.setTenantId(subTenantId);
			}
			if (GCloudExtension.getSubscriptionId() == null) {
				GCloudExtension.setSubscriptionId(subscriptionId);
			}
		}
		*/
	}
}
