package com.github.rmee.az;

import groovy.json.JsonSlurper;
import org.gradle.api.tasks.TaskAction;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class AzLoginTask extends AzExec {

	@TaskAction
	public void run() {
		AzExtension azExtension = getProject().getExtensions().getByType(AzExtension.class);
		String userName = azExtension.getUserName();
		String password = azExtension.getPassword();
		String tenantId = azExtension.getTenantId();
		boolean servicePrincipal = azExtension.isServicePrincipal();

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

		captureOutput = true;

		System.out.println(command);
		setCommand(command.toString());

		super.run();

		if (azExtension.getSubscriptionId() == null || azExtension.getTenantId() == null) {
			List logins = (List) new JsonSlurper().parseText(output);
			if (logins.size() != 1) {
				throw new IllegalStateException("only single subscription implemented, got " + output);
			}
			Map subscription = (Map) logins.get(0);
			String subscriptionId = Objects.requireNonNull((String) subscription.get("id"));
			String subTenantId = Objects.requireNonNull((String) subscription.get("tenantId"));

			if (azExtension.getTenantId() == null) {
				azExtension.setTenantId(subTenantId);
			}
			if (azExtension.getSubscriptionId() == null) {
				azExtension.setSubscriptionId(subscriptionId);
			}
		}
		System.out.println(output);
	}
}
