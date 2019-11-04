package com.github.rmee.cli.base;

import com.github.rmee.cli.base.internal.CliExecBase;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.TaskAction;

public class CliExec extends CliExecBase {

	private CliExecSpec spec = new CliExecSpec();

	@TaskAction
	public void exec() {
		CliExecExtension extension = getProject().getExtensions().getByType(CliExecExtension.class);
		extension.exec(spec);
	}

	@Override
	@Input
	protected CliExecSpec retrieveSpec() {
		return spec;
	}
}
