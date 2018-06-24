package com.github.rmee.oc;

import com.github.rmee.common.Credentials;
import org.gradle.api.tasks.TaskAction;

public class OcLogin extends OcExec {

	@TaskAction
	protected void exec() {
		OcExtension extension = getProject().getExtensions().getByType(OcExtension.class);
		Credentials credentials = extension.getCredentials();

		String verifyParam = extension.isInsecureSkipTlsVerify() ? " --insecure-skip-tls-verify=true" : "";

		if (credentials.getToken() != null) {
			commandLine("oc login " + verifyParam + " --token=" + credentials.getToken() + " " + extension.getUrl());
		} else {
			commandLine("oc login " + verifyParam + " --username=" + extension.getCredentials().getUserName()
					+ " --password=" + credentials.getPassword() + " " + extension.getUrl());
		}
		super.exec();
	}


}
