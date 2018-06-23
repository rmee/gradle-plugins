package com.github.rmee.az;

import com.github.rmee.common.ClientExecSpec;
import com.github.rmee.common.internal.ClientExecBase;
import org.gradle.api.tasks.TaskAction;

public class AzExec extends ClientExecBase {

	private AzExecSpec spec = new AzExecSpec();

	public AzExec() {
		setGroup("provision");
	}

	@TaskAction
	public void run() {
		AzExtension extension = getProject().getExtensions().getByType(AzExtension.class);
		extension.exec(spec);
	}

	@Override
	protected ClientExecSpec retrieveSpec() {
		return spec;
	}
}
