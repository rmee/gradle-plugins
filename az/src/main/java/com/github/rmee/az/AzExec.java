package com.github.rmee.az;

import com.github.rmee.cli.base.CliExecSpec;
import com.github.rmee.cli.base.internal.CliExecBase;
import org.gradle.api.tasks.TaskAction;

public class AzExec extends CliExecBase {

	private AzExecSpec spec = new AzExecSpec();

	@TaskAction
	public void run() {
		AzExtension extension = getProject().getExtensions().getByType(AzExtension.class);
		extension.exec(spec);
	}

	@Override
	protected CliExecSpec retrieveSpec() {
		return spec;
	}
}
