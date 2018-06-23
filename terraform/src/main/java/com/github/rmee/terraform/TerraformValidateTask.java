package com.github.rmee.terraform;

public class TerraformValidateTask extends TerraformExec {

	public TerraformValidateTask() {
		setCommandLine("validate");
	}
}
