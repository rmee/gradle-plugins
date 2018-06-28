package com.github.rmee.terraform;

public class TerraformRefreshTask extends TerraformExec {

	public TerraformRefreshTask() {
		commandLine("refresh");
	}
}
