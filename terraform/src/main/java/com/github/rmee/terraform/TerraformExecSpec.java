package com.github.rmee.terraform;

import com.github.rmee.cli.base.CliExecSpec;

public class TerraformExecSpec extends CliExecSpec<TerraformExecSpec> {

	private boolean addConfigDirectory = true;

	private boolean addVariables = true;

	public boolean getAddConfigDirectory() {
		return addConfigDirectory;
	}

	public void setAddConfigDirectory(boolean addConfigDirectory) {
		this.addConfigDirectory = addConfigDirectory;
	}

	public boolean getAddVariables() {
		return addVariables;
	}

	public void setAddVariables(boolean addVariables) {
		this.addVariables = addVariables;
	}

	@Override
	public void duplicate(TerraformExecSpec duplicate) {
		duplicate.addConfigDirectory = addConfigDirectory;
		duplicate.addVariables = addVariables;
	}
}
