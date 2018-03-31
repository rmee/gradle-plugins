package com.github.rmee.kubernetes.oc;

import com.github.rmee.kubernetes.common.Credentials;
import org.gradle.api.tasks.TaskAction;

public class OcLogin extends OcExec {

	@TaskAction
	protected void exec() {
		OcExtension extension = getProject().getExtensions().getByType(OcExtension.class);
		Credentials credentials = extension.getCredentials();
		if (credentials.getToken() != null) {
			setCommandLine("oc login --token=" + credentials.getToken() + " " + extension.getUrl());
		}
		else {
			setCommandLine("oc login --username=" + extension.getCredentials().getUserName()
					+ " --password=" + credentials.getPassword() + " " + extension.getUrl());
		}
		super.exec();
	}


}
