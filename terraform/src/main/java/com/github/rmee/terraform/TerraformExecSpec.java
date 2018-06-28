package com.github.rmee.terraform;

import com.github.rmee.common.ClientExecSpec;

public class TerraformExecSpec extends ClientExecSpec<TerraformExecSpec> {

	private boolean addConfigDirectory = true;

	private boolean addVariables = true;

	protected boolean getAddConfigDirectory() {
		return addConfigDirectory;
	}

	protected void setAddConfigDirectory(boolean addConfigDirectory) {
		this.addConfigDirectory = addConfigDirectory;
	}

	protected boolean getAddVariables() {
		return addVariables;
	}

	protected void setAddVariables(boolean addVariables) {
		this.addVariables = addVariables;
	}

	@Override
	protected TerraformExecSpec newSpec() {
		return new TerraformExecSpec();
	}

	@Override
	protected void duplicate(TerraformExecSpec duplicate) {
		duplicate.addConfigDirectory = addConfigDirectory;
		duplicate.addVariables = addVariables;
	}
}
