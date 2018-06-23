package com.github.rmee.helm;

import com.github.rmee.common.ClientExecSpec;
import com.github.rmee.common.internal.ClientExecBase;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.TaskAction;

public class HelmExec extends ClientExecBase {

	private HelmExecSpec spec = new HelmExecSpec();

	public HelmExec() {
		setGroup("kubernetes");

		dependsOn("helmBootstrap");
	}

	@TaskAction
	public void exec() {
		HelmExtension extension = getProject().getExtensions().getByType(HelmExtension.class);
		extension.exec(spec);
	}

	@Input
	@Override
	protected ClientExecSpec getSpec() {
		return spec;
	}
}
