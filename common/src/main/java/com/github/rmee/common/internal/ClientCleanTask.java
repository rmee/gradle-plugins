package com.github.rmee.common.internal;

import com.github.rmee.common.Client;
import com.github.rmee.common.ClientExtensionBase;
import org.gradle.api.DefaultTask;
import org.gradle.api.Project;

public class ClientCleanTask extends DefaultTask {

	public ClientCleanTask(Class<? extends ClientExtensionBase> extensionClass) {
		doFirst(task -> {
			Project project = getProject();
			ClientExtensionBase extension = project.getExtensions().findByType(extensionClass);
			Client client = extension.getClient();
			client.deleteOutputFiles();
		});
	}
}
