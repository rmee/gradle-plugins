package com.github.rmee.terraform;

public class TerraformInitTask extends TerraformExec {

	public TerraformInitTask() {
		setCommandLine("init");
	}
}
