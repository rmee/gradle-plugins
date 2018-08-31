package com.github.rmee.gcloud;

import groovy.json.JsonSlurper;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class GCloudLoginTask extends DefaultTask {

	@TaskAction
	public void run() {
		GCloudExtension GCloudExtension = getProject().getExtensions().getByType(GCloudExtension.class);
		String userName = GCloudExtension.getUserName();
		String password = GCloudExtension.getPassword();
		String tenantId = GCloudExtension.getTenantId();
		boolean servicePrincipal = GCloudExtension.isServicePrincipal();

		StringBuilder command = new StringBuilder();
		command.append("az login");
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

		File tempFile;
		try {
			tempFile = File.createTempFile("login", "json");
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}

		GCloudExecSpec execSpec = new GCloudExecSpec();
		execSpec.setCommandLine(command.toString());
		execSpec.setStdoutFile(tempFile);
		GCloudExtension.exec(execSpec);

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
	}
}
