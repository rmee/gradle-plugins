package com.github.rmee.terraform;

import com.github.rmee.cli.base.internal.ClientExecBase;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.TaskAction;

public class TerraformExec extends ClientExecBase {

	public static final String CONTAINER_WORKING_DIRECTORY = ".terraform";

	private TerraformExecSpec spec = new TerraformExecSpec();

	public TerraformExec() {
		setGroup("provision");
	}

	@TaskAction
	public void exec() {
		TerraformExtension extension = getProject().getExtensions().getByType(TerraformExtension.class);
		extension.exec(spec);
	}

	@Override
	@Input
	protected TerraformExecSpec retrieveSpec() {
		return spec;
	}

	public boolean getAddVariables() {
		return spec.getAddVariables();
	}

	public void setAddVariables(boolean addVariables) {
		this.spec.setAddVariables(addVariables);
	}
}
