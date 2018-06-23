package com.github.rmee.kubectl;

import org.gradle.api.tasks.TaskAction;

public class KubectlExec extends KubectlExecBase {

	public KubectlExec() {
		dependsOn("kubectlBootstrap");
	}

	@TaskAction
	protected void exec() {
		KubectlExtension extension = getProject().getExtensions().getByType(KubectlExtension.class);
		result = extension.exec(spec);
	}
}
